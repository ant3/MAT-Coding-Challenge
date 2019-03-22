package com.purpleit.mclarenchallenge;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.purpleit.mclarenchallenge.model.Car;
import com.purpleit.mclarenchallenge.model.CarStatusDto;
import com.purpleit.mclarenchallenge.model.CarCoordinatesDto;
import com.purpleit.mclarenchallenge.model.Race;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.IOException;


/**
 * This is where all the actual work happens.
 * This class defines the data processing pipelne.
 *
 * The basic processing flow (ignoring plumbing) is:
 *
 *       mqttInbound
 *           |
 *        parseJson
 *           |
 *        trackCar
 *        /     \
 * findSpeed   findPosition
 *        \     /
 *     serialiseStatus
 *           |
 *    mqttOutboundStatus
 */
@Slf4j
@Configuration
@EnableIntegration
public class DataPipeline {

    @Autowired
    private Race race;

    ObjectMapper objectMapper = new ObjectMapper();


    @Bean
    public MessageChannel jsonChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://localhost:1883" });
        options.setUserName("guest");
        options.setPassword("guest".toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(@Autowired MqttPahoClientFactory factory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("input",
                factory, "carCoordinates");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(jsonChannel());
        return adapter;
    }

    @Bean
    public MessageChannel rawUpdateChannel() {
        return new DirectChannel();
    }

    @Transformer(inputChannel="jsonChannel", outputChannel="rawUpdateChannel")
    public CarCoordinatesDto parseJson(Message<String> msg) {
        log.info("Building object from JSON: {}", msg.getPayload());
        try {
            CarCoordinatesDto dto = objectMapper.readValue(msg.getPayload(), CarCoordinatesDto.class);
            return dto;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    public MessageChannel enrichedUpdateChannel() {
        return new PublishSubscribeChannel();
    }

    @Transformer(inputChannel="rawUpdateChannel", outputChannel="enrichedUpdateChannel")
    public Car trackCar(Message<CarCoordinatesDto> msg) {
        return race.updateCar(msg.getPayload());
    }

//    @Bean
//    @ServiceActivator(inputChannel="enrichedUpdateChannel")
//    public LoggingHandler logger() {
//        LoggingHandler loggingHandler = new LoggingHandler("INFO");
//        loggingHandler.setLoggerName("enriched");
//        return loggingHandler;
//    }

    @Bean
    public MessageChannel speedUpdateChannel() {
        return new DirectChannel();
    }

    @Transformer(inputChannel="enrichedUpdateChannel", outputChannel="speedUpdateChannel")
    public CarStatusDto findSpeed(Message<Car> msg) {
        Car car = msg.getPayload();
        return new CarStatusDto(car.getEpochMillis(), car.getId(), CarStatusDto.ValueType.SPEED, car.getSpeed());
    }

    @Bean
    public MessageChannel positionUpdateChannel() {
        return new DirectChannel();
    }

    @Transformer(inputChannel="enrichedUpdateChannel", outputChannel="positionUpdateChannel")
    public CarStatusDto findPosition(Message<Car> msg) {
        Car car = msg.getPayload();
        return new CarStatusDto(car.getEpochMillis(), car.getId(), CarStatusDto.ValueType.POSITION, car.getPosition());
    }

    @Bean
    public MessageChannel statusUpdateChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel="speedUpdateChannel")
    public BridgeHandler bridgeSpeed() {
        BridgeHandler handler = new BridgeHandler();
        handler.setOutputChannelName("statusUpdateChannel");
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel="positionUpdateChannel")
    public BridgeHandler bridgePosition() {
        BridgeHandler handler = new BridgeHandler();
        handler.setOutputChannelName("statusUpdateChannel");
        return handler;
    }

    @Bean
    public MessageChannel statusJsonChannel() {
        return new DirectChannel();
    }

    @Transformer(inputChannel="statusUpdateChannel", outputChannel="statusJsonChannel")
    public String serialiseStatus(Message<CarStatusDto> msg) {
        try {
            String json = objectMapper.writeValueAsString(msg.getPayload());
            log.info("Sending JSON: {}", json);
            return json;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Bean
    @ServiceActivator(inputChannel="statusJsonChannel")
    public MqttPahoMessageHandler mqttOutboundStatus(@Autowired MqttPahoClientFactory factory) {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler("output", factory);
        handler.setDefaultTopic("carStatus");
        return handler;
    }
}

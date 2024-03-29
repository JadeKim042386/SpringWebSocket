package joo.example.messagewithrabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Sending message...");
        String message = "Hello from RabbitMQ!";
        rabbitTemplate.convertAndSend(RabbitMQConfig.topicExchangeName, "foo.bar.baz", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.directExchangeName, "direct", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.fanoutExchangeName, "", message);

        Message messageWithHeader = MessageBuilder.withBody(message.getBytes())
                .setHeader("headers", "h")
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.headersExchangeName, "", messageWithHeader);
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }

}

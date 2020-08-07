package org.hilel14.archie.enhabsor.core.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

/**
 *
 * @author hilel-deb
 */
public class JmsProducer {

    static final Logger LOGGER = LoggerFactory.getLogger(JmsProducer.class);
    ConnectionFactory connectionFactory;
    String queueName;

    public JmsProducer(ConnectionFactory connectionFactory, String queueName) throws NamingException {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
    }

    private void produce(String text, String archieJobName) throws JMSException {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queueName);
            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            // producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // Create a messages
            TextMessage message = session.createTextMessage(text);
            message.setStringProperty("archieJobName", archieJobName);
            // Tell the producer to send the message
            producer.send(message);
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void produceJsonMessage(Object object, String archieJobName) throws JsonProcessingException, JMSException {
        String result = new ObjectMapper().writeValueAsString(object);
        produce(result, archieJobName);
    }

    public void produceTextMessage(String msg, String archieJobName) throws JMSException {
        produce(msg, archieJobName);
    }
}

package com.crew.camel

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.camel.Body
import org.apache.camel.CamelContext
import org.apache.camel.Consume
import org.apache.camel.Endpoint
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.Header
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.io.ClassPathResource
import java.io.Closeable
import java.util.Date
import java.util.UUID

class Application: Closeable {
    private val context: GenericApplicationContext = GenericApplicationContext()
    lateinit var producer: Producer

    init {
        val xmlReader = XmlBeanDefinitionReader(context)
        xmlReader.loadBeanDefinitions(
            ClassPathResource("applicationContext.xml")
        )
        context.refresh()
        context.beanFactory.autowireBeanProperties(this, AbstractAutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false)
    }

    override fun close() {
        context.close()
    }

    fun run() {
        val schedule = Schedule(
            UUID.randomUUID().toString(),
            Date(),
            Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 90)
        )

        //sends a message synchronously
        producer.sendEvent(schedule, EventType.CREATE)

        //sends a message asynchronously
        producer.sendAsyncEvent(schedule, EventType.CREATE)
    }
}

class Producer(private val camelContext: CamelContext,
               private val objectMapper: ObjectMapper) {
    @Produce
    private lateinit var producerTemplate: ProducerTemplate

    @EndpointInject(ref = "scheduleEvent")
    private lateinit var scheduleEventEndpoint: Endpoint

    @EndpointInject(ref = "asyncScheduleEvent")
    private lateinit var asyncScheduleEventEndpoint: Endpoint


    fun sendEvent(schedule: Schedule, eventType: EventType) {
        producerTemplate.send(
            scheduleEventEndpoint,
            exchangeBuilder()
                .withHeader("event", eventType.name)
                .withBody(objectMapper.writeValueAsString(schedule))
                .build()
        )
    }

    fun sendAsyncEvent(schedule: Schedule, eventType: EventType) {
        producerTemplate.send(
            asyncScheduleEventEndpoint,
            exchangeBuilder()
                .withHeader("event", eventType.name)
                .withBody(objectMapper.writeValueAsString(schedule))
                .build()
        )
    }

    private fun exchangeBuilder(): ExchangeBuilder {
        return ExchangeBuilder.anExchange(camelContext)
    }
}

class Consumer {
    @Consume(ref ="sendCreateNotification")
    fun processScheduleCreate(@Body schedule: Schedule, @Header("event") event: EventType) {
        println("Sending out that notification for a new schedule. $schedule")
    }

    @Consume(ref ="postScheduleUpdate")
    fun processScheduleUpdate(@Body schedule: Schedule, @Header("event") event: EventType) {
        println("The schedule has been updated: $schedule")
    }

    @Consume(ref ="postScheduleDelete")
    fun processScheduleDelete(@Body schedule: Schedule, @Header("event") event: EventType) {
        println("Bummer someone deleted the schedule. $schedule")
    }

    fun enrich(exchange: Exchange) {
        exchange.out.headers["newHeader"] = "a great header value"
    }
}

fun main(args: Array<String>) {
    var running = true
    Application().use { app ->

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                running = false
                println("Shutting down")
            }
        })

        while (running) {
            app.run()
            Thread.sleep(1000)
        }
    }
}
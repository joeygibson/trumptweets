package com.joeygibson.trumptweets

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler
import mu.KotlinLogging

/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience. To do this, simply set the handler field in the AWS Lambda console to
 * "helloworld.HelloWorldSpeechletRequestStreamHandler" For this to work, you'll also need to build
 * this project using the `lambda-compile` Ant task and upload the resulting zip file to power
 * your function.
 */
class TrumpTweetsSpeechletRequestStreamHandler : SpeechletRequestStreamHandler(TrumpTweetsSpeechlet(), TrumpTweetsSpeechletRequestStreamHandler.supportedApplicationIds) {
    companion object {
        private val supportedApplicationIds = HashSet<String>()
        private val log = KotlinLogging.logger {}

        init {
            /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
            val appId = System.getenv("APP_ID") ?: "NONE"

            log.info { "APP_ID = ${appId}" }

            supportedApplicationIds.add(appId)
        }
    }
}
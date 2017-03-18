package com.joeygibson.trumptweets

import com.amazon.speech.speechlet.*
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import mu.KotlinLogging
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.OAuth2Token
import twitter4j.conf.ConfigurationBuilder
import java.lang.System.currentTimeMillis

class TrumpTweetsSpeechlet : Speechlet {
    val log = KotlinLogging.logger {}

    val THRESHOLD = 900000L
    var lastTweet = ""
    var lastTweetTime = 0L

    private var twitter: Twitter? = null
    private var token: OAuth2Token? = null

    override fun onSessionStarted(request: SessionStartedRequest?, session: Session?) {
        log.info("onSessionStarted requestId=${request?.requestId}, sessionId=${session?.sessionId}")
    }

    override fun onSessionEnded(request: SessionEndedRequest?, session: Session?) {
        log.info("onSessionEnded requestId=${request?.requestId}, sessionId=${session?.sessionId}")
    }

    override fun onIntent(request: IntentRequest?, session: Session?): SpeechletResponse {
        log.info("onIntent requestId=${request?.requestId}, sessionId=${session?.sessionId}")

        val intent = request?.intent
        val intentName = intent?.name

        return when (intentName) {
            "TweetIntent" -> getTweetResponse()
            "AMAZON.HelpIntent" -> getHelpResponse()
            else -> throw SpeechletException("Invalid Intent")
        }
    }

    private fun getHelpResponse(): SpeechletResponse {
        val speechText = "You can ask what did he say now?"

        val card = SimpleCard()
        card.title = "TrumpTweets"
        card.content = speechText

        val speech = PlainTextOutputSpeech()
        speech.text = speechText

        val reprompt = Reprompt()
        reprompt.outputSpeech = speech

        return SpeechletResponse.newAskResponse(speech, reprompt, card)
    }

    private fun getTweetResponse(): SpeechletResponse {
        val speechText = getTrumpsLatestTweet()

        val card = SimpleCard()
        card.title = "TrumpTweets"
        card.content = speechText

        val speech = PlainTextOutputSpeech()
        speech.text = speechText

        return SpeechletResponse.newTellResponse(speech, card)
    }

    override fun onLaunch(request: LaunchRequest?, session: Session?): SpeechletResponse {
        log.info("onLaunch requestId=${request?.requestId}, sessionId=${session?.sessionId}")

        authenticateToTwitter()

        return getWelcomeResponse()
    }

    private fun getWelcomeResponse(): SpeechletResponse {
        val speechText = "Hear Trump's latest absurd tweet."

        val card = SimpleCard()
        card.title = "TrumpTweets"
        card.content = speechText

        val speech = PlainTextOutputSpeech()
        speech.text = speechText

        val reprompt = Reprompt()
        reprompt.outputSpeech = speech

        return SpeechletResponse.newAskResponse(speech, reprompt, card)
    }

    fun getTrumpsLatestTweet(): String {
        authenticateToTwitter()
        val now = currentTimeMillis()

        if (now - lastTweetTime > THRESHOLD) {
            twitter?.let { twitter ->
                val timeline = twitter.getUserTimeline("realdonaldtrump")

                val tweet = timeline.first()
                val tweetText = tweet.text
                        .replace("""\S+://\S+""".toRegex(), "")
                        .replace("""\s+""".toRegex(), " ")
                        .replace("@", "")

                lastTweet = "${tweet.createdAt}, $tweetText"
                lastTweetTime = now
            }
        }

        return lastTweet
    }

    fun authenticateToTwitter() {
        if (twitter != null && token != null) {
            return
        }

        val cb = ConfigurationBuilder()
        cb.setApplicationOnlyAuthEnabled(true)

        twitter = TwitterFactory(cb.build()).instance
        twitter?.let {
            it.setOAuthConsumer(System.getenv("CONSUMER_KEY"),
                    System.getenv("CONSUMER_SECRET_KEY"))

            token = it.oAuth2Token
        }
    }
}
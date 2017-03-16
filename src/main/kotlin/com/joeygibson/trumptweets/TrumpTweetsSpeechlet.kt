package com.joeygibson.trumptweets

import com.amazon.speech.speechlet.*
import com.amazon.speech.ui.PlainTextOutputSpeech
import com.amazon.speech.ui.Reprompt
import com.amazon.speech.ui.SimpleCard
import mu.KotlinLogging
import org.jsoup.Jsoup
import java.lang.System.currentTimeMillis

class TrumpTweetsSpeechlet : Speechlet {
    val log = KotlinLogging.logger {}

    val THRESHOLD = 900000L
    var lastTweet = ""
    var lastTweetTime = 0L

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
        val now = currentTimeMillis()

        if (now - lastTweetTime > THRESHOLD) {
            val doc = Jsoup.connect("http://twitter.com/realdonaldtrump").get()

            val tweet = doc.select("div.tweet").first()
            val tweetUrl = tweet.attr("data-permalink-path")
            val fullUrl = "http://twitter.com${tweetUrl}"

            val tweetDoc = Jsoup.connect(fullUrl).get()
            var tweetText = tweetDoc.select("p.tweet-text").first().ownText()

            tweetText = tweetText
                    .replace("""\S+://\S+""".toRegex(), "")
                    .replace("""\s+""".toRegex(), " ")
                    .replace("@", "")

            val dateTime = tweetDoc.select("span.metadata").first()
                    .child(0).ownText()

            lastTweet = "$dateTime, $tweetText"
            lastTweetTime = now
        }

        return lastTweet
    }
}
# TrumpTweets

An [Alexa skill](https://www.amazon.com/b?ie=UTF8&node=13727921011) that fetches the latest 
tweet from [@RealDonaldTrump](https://twitter.com/RealDonaldTrump) and reads it.

## Building

Once you have setup the skill in the Alexa dashboard, you need to copy the app id
and set an environment variable called `APP_ID` with that value. Then run 
`mvn clean package` again to generate a jar file that can only be called by
that skill. Re-upload the jar file, and you should be ready to go.



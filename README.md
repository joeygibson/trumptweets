# TrumpTweets

An [Alexa skill](https://www.amazon.com/b?ie=UTF8&node=13727921011) that fetches the latest 
tweet from [@RealDonaldTrump](https://twitter.com/RealDonaldTrump) and reads it.

## Building

```
mvn clean package
```

## Installing

Create an [AWS Lambda](https://aws.amazon.com/lambda/) function with a runtime of `Java 8`. 
Upload the jar file, and save it. Look in the top right for the `ARN`.

Next go to the [Amazon Developer Portal](https://developer.amazon.com/edw/home.html#/) and
add a new skill. Tie it to the `ARN` that the Lambda console gave you and save it. Then take
the "Application Id" that is on the "Skill Information" tab, and go back to the Lambda page. On the 
"Code" tab, add an environment variable called `APP_ID`, and set its value to the application Id
you got from the skill page.

You will also need to set two additional environment variables to enable the Twitter integration. They are

* CONSUMER_KEY
* CONSUMER_SECRET_KEY

The values for these can be found by logging in to [the Twitter apps page](https://apps.twitter.com).

Save it and test.



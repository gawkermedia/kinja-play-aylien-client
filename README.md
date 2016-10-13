# Scala client for Aylien text analysis API

It's a [Play! 2.5 WS](https://www.playframework.com/documentation/2.5.x/ScalaWS) based API client for [Aylien's text analysis API](http://aylien.com/text-api).

## Usage example

```scala
import play.api.libs.json.{ JsSuccess, JsError }
import com.kinja.play.aylien.textapi._, model._

val config = TextApiClientConfig(
	new java.net.URL("http://api.aylien.com/api/v1"),
	System.getenv("AYLIEN_APP_ID"),
	System.getenv("AYLIEN_APP_KEY"),
	"iab-qag",
	scala.concurrent.duration.Duration("5 seconds"),
	play.api.libs.ws.ning.NingWSClient(),
	scala.concurrent.ExecutionContext.Implicits.global)

val api = new TextApiClient(config)

api.classifyByTaxonomy(new java.net.URL("http://techcrunch.com/2015/07/16/microsoft-will-never-give-up-on-mobile")).map { r => 
	r match {
		case s: JsSuccess[ClassifyTaxonomy] => println(s);
		case e: JsError => println("Errors: " + JsError.toFlatJson(e).toString())
	} 
} recover { case e: AylienException => println(e.message) }

```

## Supported API calls:

 - [classifyByTaxonomy](http://docs.aylien.com/docs/classify-taxonomy)

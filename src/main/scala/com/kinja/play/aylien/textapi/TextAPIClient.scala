package com.kinja.play.aylien.textapi

import java.net.URL
import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.duration.Duration
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.{ WSResponse, WSRequest, WSClient }
import play.api.http.{ HeaderNames, MimeTypes }
import com.kinja.play.aylien.textapi.model._

/**
 * Configuration for [[TextApiClient]].
 *
 * @param apiUrl The base URL of the Aylien API (without trailing slash)
 * @param appId The Id of the Aylien APP
 * @param appKey The key for the Aylien APP
 * @param taxonomy The taxonomy type for classification: http://docs.aylien.com/docs/classify-taxonomy
 * @param requestTimeout The maximum amount of time to wait for a GET request to complete.
 * @param client An instance of the Play WS client.
 * @param executionContext The thread pool to run the API requests in.
 */
final case class TextApiClientConfig(
	apiUrl: URL,
	appId: String,
	appKey: String,
	taxonomy: String,
	requestTimeout: Duration,
	client: WSClient,
	executionContext: ExecutionContext)

/**
 * A simple client for Aylien API
 *
 * Every public methods return the raw response from the Aylien API wrapped in
 * a `Future[JsResult]`
 */
final class TextApiClient(config: TextApiClientConfig) {

	private implicit val ec: ExecutionContext = config.executionContext

	private val logger = Logger("com.kinja.play.aylien")

	private def get(url: URL, params: Seq[(String, String)]): Future[WSResponse] = {
		val request = config.client.url(url.toString)

		logger.debug(s"GET $url")
		logger.debug(s"params: $params")

		request
			.withHeaders(
				HeaderNames.ACCEPT -> MimeTypes.JSON,
				"X-AYLIEN-TextAPI-Application-Key" -> config.appKey,
				"X-AYLIEN-TextAPI-Application-ID" -> config.appId)
			.withRequestTimeout(config.requestTimeout.toMillis.toLong)
			.withQueryString(params:_*)
			.get()
	}

	/**
	 * http://docs.aylien.com/docs/classify-taxonomy
	 * 
	 * @param url The URL to analyise
	 * @return A classification object
	 */
	def classifyByTaxonomy(url: URL): Future[JsResult[ClassifyTaxonomy]] = {
		val params = Seq(("url" -> url.toString))
		get(new URL(config.apiUrl + "/classify/" + config.taxonomy), params).map { response =>
			response.json.validate[ClassifyTaxonomy]
		}
	}

}

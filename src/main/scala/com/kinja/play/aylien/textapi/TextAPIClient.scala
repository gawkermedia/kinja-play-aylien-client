package com.kinja.play.aylien.textapi

import java.net.URL
import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.duration.Duration
import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.{ WSResponse, WSRequest, WSClient }
import play.api.http.{ HeaderNames, MimeTypes }
import com.kinja.play.aylien.textapi.model._

case class AylienException(code: Int, message: String) extends Exception(message)

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
			.withRequestTimeout(config.requestTimeout)
			.withQueryString(params:_*)
			.get()
	}

	private def post(url: URL, params: Map[String, Seq[String]]): Future[WSResponse] = {
		val request = config.client.url(url.toString)

		logger.debug(s"POST $url")
		logger.debug(s"params: $params")

		request
			.withHeaders(
				HeaderNames.ACCEPT -> MimeTypes.JSON,
				"X-AYLIEN-TextAPI-Application-Key" -> config.appKey,
				"X-AYLIEN-TextAPI-Application-ID" -> config.appId)
			.withRequestTimeout(config.requestTimeout)
			.post(params)
	}

	/**
	 * http://docs.aylien.com/docs/classify-taxonomy
	 * 
	 * @param url The URL to analyise
	 * @return A classification object or an [[AylienException]] on error
	 */
	def classifyByTaxonomy(url: URL): Future[JsResult[ClassifyTaxonomy]] = {
		val params = Map("url" -> Seq(url.toString))
		post(new URL(config.apiUrl + "/classify/" + config.taxonomy), params).flatMap { response =>
			if (response.status == 200) {
				Future.successful(response.json.validate[ClassifyTaxonomy])
			} else {
				Future.failed(AylienException(response.status, (response.json \ "error").as[String]))
			}
		}
	}

	/**
	 * http://docs.aylien.com/docs/classify-taxonomy
	 * 
	 * @param text The text to analyise
	 * @return A classification object or an [[AylienException]] on error
	 */
	def classifyByTaxonomy(text: String): Future[JsResult[ClassifyTaxonomy]] = {
		val params = Map("text" -> Seq(text.replaceAll("\\<.*?\\>", ""))) 
		post(new URL(config.apiUrl + "/classify/" + config.taxonomy), params).flatMap { response =>
			if (response.status == 200) {
				Future.successful(response.json.validate[ClassifyTaxonomy])
			} else {
				Future.failed(AylienException(response.status, (response.json \ "error").as[String]))
			}
		}
	}

}

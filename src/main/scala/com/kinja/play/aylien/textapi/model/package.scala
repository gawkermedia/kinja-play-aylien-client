package com.kinja.play.aylien.textapi.model

import play.api.libs.json._

final case class TaxonomyLink(
	link: String,
	rel: String)

object TaxonomyLink {
	implicit val taxonomyLinkReads = Json.reads[TaxonomyLink]
}

final case class Category(
	id: String,
	confident: Boolean,
	score: Option[Float],
	label: String,
	links: Set[TaxonomyLink])

object Category {
	implicit val categoryReads = Json.reads[Category]
}

final case class ClassifyTaxonomy(
	categories: Seq[Category],
	language: String,
	taxonomy: String,
	text: String)

object ClassifyTaxonomy {
	implicit val classifyTaxonomyReads = Json.reads[ClassifyTaxonomy]
}


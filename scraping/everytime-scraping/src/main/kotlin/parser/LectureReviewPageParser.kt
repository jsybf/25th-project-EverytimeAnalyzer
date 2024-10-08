package org.example.parser

import org.example.entity.LectureReview
import org.jsoup.nodes.Document

object ReviewDivCssSelector {
    val content: String = ".text"
    val stars: String = ".star > .on"
    val semester: String = ".semester"

}

object LectureReviewPageCssSelector {
    val reviewList: String = "body .article_tab div.articles > div.article"
}


object LectureReviewPageParser {
    val yearSemesterRegex: Regex = Regex("""(?<year>[0-9]{2})년\s(?<semester>[1-2]{1}학기|여름|겨울)*\s수강자""")
    val extractNum: Regex = Regex("[0-9]+")

    fun extractYearAndSemester(raw: String): Pair<Int, String> {
        val groups: MatchGroupCollection = yearSemesterRegex.find(raw)?.groups
            ?: throw IllegalStateException("regex match failed input:${raw}")

        return Pair(("20" + groups["year"]!!.value).toInt(), groups["semester"]!!.value)
    }

    fun parse(lectureCode: String, doc: Document): List<LectureReview> {
        return doc.select(LectureReviewPageCssSelector.reviewList)
            .map {
                val text: String = it.select(ReviewDivCssSelector.content).text()
                val rawSemester: String = it.select(ReviewDivCssSelector.semester).text()
                val rawStars: String = it.select(ReviewDivCssSelector.stars).attr("style")
                val rate: Int = extractNum.find(rawStars)!!.value.toInt()

                val (year, semester) = extractYearAndSemester(rawSemester)
                LectureReview(year, semester, text, rate)
            }
    }
}


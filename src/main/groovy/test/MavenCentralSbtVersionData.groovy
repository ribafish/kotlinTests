package test

import groovy.xml.XmlSlurper

class MavenCentralSbtVersionData {

    static void main(String[] args) {
//        testy()
        println(latestRelease("com.nrinaudo", "kantan.sbt"))
        println(latestRelease("org.jetbrains", "sbt-declarative-core"))
        println(latestRelease("com.gradle", "sbt-develocity"))
    }

    static String latestRelease(String group, String name) {
        String url = "https://search.maven.org/solrsearch/select?q=g:$group+AND+a:$name&rows=1&wt=xml"
        def parsed = new XmlSlurper().parse(url)
        def value = new XmlSlurper().parse(url).result.doc.str.find { it.'@name' == "latestVersion" }
        return "$group:$name:$value"
    }


    static void testy() {
        def xml = '<articles><article><title>First steps in Java</title><author id="1"><firstname>Siena</firstname><lastname>Kerr</lastname></author><release-date>2018-12-01</release-date></article><article><title>Dockerize your SpringBoot application</title><author id="2"><firstname>Jonas</firstname><lastname>Lugo</lastname></author><release-date>2018-12-01</release-date></article><article><title>SpringBoot tutorial</title><author id="3"><firstname>Daniele</firstname><lastname>Ferguson</lastname></author><release-date>2018-06-12</release-date></article><article><title>Java 12 insights</title><author id="1"><firstname>Siena</firstname><lastname>Kerr</lastname></author><release-date>2018-07-22</release-date></article></articles>'
        def xmlFile = new ByteArrayInputStream(xml.getBytes())
        def articles = new XmlSlurper().parse(xmlFile)

        println articles.'*'.size() == 4
        println articles.article[0].author.firstname == "Siena"
        println articles.article[2].'release-date' == "2018-06-12"
        println articles.article[3].title == "Java 12 insights"
        println articles.article.find { it.author.'@id' == "3" }.author.firstname == "Daniele"
    }

}
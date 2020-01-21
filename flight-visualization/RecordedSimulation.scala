package kafka

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("https://chart.googleapis.com")
		.inferHtmlResources(BlackList(""".*.ico""", """,*,css"""), WhiteList())
		.acceptHeader("image/webp,*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (X11; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0")

	val headers_3 = Map(
		"Accept" -> "text/css,*/*;q=0.1",
		"Pragma" -> "no-cache")

	val headers_4 = Map("Pragma" -> "no-cache")

	val headers_19 = Map(
		"Accept" -> "*/*",
		"Pragma" -> "no-cache")

    val uri2 = "https://fonts.googleapis.com/css"
    val uri3 = "https://www.gstatic.com/charts/45.1"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:pppsrpppppppqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqppppppppqqqqrrrrrrrrrssrrrssrrrsrrrrqqqqqqqqqqqmmmmmmmmmmmmqqqqqqqqqqqqqqqqqqqqqqqqpppqqrrrqqqqqq,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=MA|Dane&chdls=0ce3ac,20&chxs=0N*,3498db"))
		.pause(3)
		.exec(http("request_1")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:pppsrpppppppqqqqqpqqqqqqqqqqqqqqrrrrqqqqqqqqqqqqqqqqqqqqpppppppppqqqqqqqrrrrrssrrrrsrrsssrrrrrqqqqqqqqqpooonmmllkkjqqqqqqqqqqqqqqqqqqqqqqqqppppqqqqrrrrrr,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=WMA|Dane&chdls=0ce3ac,20&chxs=0N*,3498db"))
		.pause(2)
		.exec(http("request_2")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:ppprsrqpppppqqqpppppqqqsusqqqppppppqqqpppqssrpppppppppqqpppppppqrrsttrqssqprttrrsssrqpqrqpqqpppqqqppppXSkprrrqqqqqqppqqqqqppppqqqqpppppppppppqrz1shfnpqqq,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=Trend|Dane&chdls=0ce3ac,20&chxs=0N*,3498db"))
		.pause(2)
		.exec(http("request_3")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3)
			.resources(http("request_4")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:pppsrpppppppqqqqqpqqqqqqqqqqqqqqrrrrqqqqqqqqqqqqqqqqqqqqpppppppppqqqqqqqrrrrrssrrrrsrrsssrrrrrqqqqqqqqqpooonmmllkkjqqqqqqqqqqqqqqqqqqqqqqqqppppqqqqrrrrrr,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=WMA|Dane&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)))
		.pause(3)
		.exec(http("request_5")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:pppsrpppppppqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqppppppppqqqqrrrrrrrrrssrrrssrrrsrrrrqqqqqqqqqqqmmmmmmmmmmmmqqqqqqqqqqqqqqqqqqqqqqqqpppqqrrrqqqqqq,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=MA|Dane&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)
			.resources(http("request_6")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3),
            http("request_7")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:pppsrpppppppqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqppppppppqqqqrrrrrrrrrssrrrssrrrsrrrrqqqqqqqqqqqmmmmmmmmmmmmqqqqqqqqqqqqqqqqqqqqqqqqpppqqrrrqqqqqq,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=MA|Dane&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)))
		.pause(2)
		.exec(http("request_8")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:ppqqqqqqqqqqqqqqqqqqqrrrrqqqqqqqqqqqqqqqqqqqqqqqqpppppppppqqqqrrrrrrrrrrrrrsssssrrrrrqqqqqqqqqqqqomlkkklmnooppppppqqqqqqqqqqqqqqqpppppppqqqrssrqppppppppp,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=Wyg%C5%82adzenie|Dane&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4))
		.pause(1)
		.exec(http("request_9")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3)
			.resources(http("request_10")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:ppqqqqqqqqqqqqqqqqqqqrrrrqqqqqqqqqqqqqqqqqqqqqqqqpppppppppqqqqrrrrrrrrrrrrrsssssrrrrrqqqqqqqqqqqqomlkkklmnooppppppqqqqqqqqqqqqqqqpppppppqqqrssrqppppppppp,pppsrppppppprppppppprpputppqpppppppqpppppssrpppppppppqpppppppppsqsturpruqoputsptssrrpptppprpopprppppppAppppqpppppppppqppqpppppqppqppppppppppprs9ppXrppppp&chco=76a4fb,ac7cc7&chm=&chds=-2953,1391&chxr=0,-2953,1391,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=Wyg%C5%82adzenie|Dane&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)))
		.pause(2)
		.exec(http("request_11")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:jjkllkjjjkkjjihgfefjmpppomkjihhhhggffhkmoonlkjiihhhiiihhgfeeegjmoooomjgghjklmooomlkkjjjihghjnsx39xcJAAGQZgmpqqpoonnmmlkkjjjjkkjjjiihgfdbaakx97xmeZYZbdfhj&chco=76a4fb&chm=&chds=-188.45404683529654,141.4197874011929&chxr=0,-188.45404683529654,141.4197874011929,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=Szum&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4))
		.pause(1)
		.exec(http("request_12")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3)
			.resources(http("request_13")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:jjkllkjjjkkjjihgfefjmpppomkjihhhhggffhkmoonlkjiihhhiiihhgfeeegjmoooomjgghjklmooomlkkjjjihghjnsx39xcJAAGQZgmpqqpoonnmmlkkjjjjkkjjjiihgfdbaakx97xmeZYZbdfhj&chco=76a4fb&chm=&chds=-188.45404683529654,141.4197874011929&chxr=0,-188.45404683529654,141.4197874011929,10&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac&chdl=Szum&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)))
		.pause(2)
		.exec(http("request_14")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:BBGGADDDDDCCCCCCCBBBDCCAANEBDDDDMDDDDDD9DODDDDBCFCDDDDBCCCCBBBBGE,BBGGDCCCCCCDCCCCCCBDCCKLGEEDCCCCCBDCCCCCFHIFCCCCCCBACCCCBBBBBBFFE&chco=76a4fb,ac7cc7&chm=&chds=-32.19532525641112,1295.4153577838965&chxr=0,-32.19532525641112,1295.4153577838965,10&chm=s,0000ff,0,undefined,8.0&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac,0000ff&chdl=Predykcja|Dane|Start%20prognozy&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)
			.resources(http("request_15")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3),
            http("request_16")
			.get("/chart?cht=lc&chs=1000x300&chxt=y&chd=s:BBGGADDDDDCCCCCCCBBBDCCAANEBDDDDMDDDDDD9DODDDDBCFCDDDDBCCCCBBBBGE,BBGGDCCCCCCDCCCCCCBDCCKLGEEDCCCCCBDCCCCCFHIFCCCCCCBACCCCBBBBBBFFE&chco=76a4fb,ac7cc7&chm=&chds=-32.19532525641112,1295.4153577838965&chxr=0,-32.19532525641112,1295.4153577838965,10&chm=s,0000ff,0,undefined,8.0&chf=c,s,2b3e50|bg,s,2b3e50&chco=ff0000,0ce3ac,0000ff&chdl=Predykcja|Dane|Start%20prognozy&chdls=0ce3ac,20&chxs=0N*,3498db")
			.headers(headers_4)))
		.pause(3)
		.exec(http("request_17")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3))
		.pause(2)
		.exec(http("request_18")
			.get(uri2 + "?family=Lato:300,400,700")
			.headers(headers_3)
			.resources(http("request_19")
			.get(uri3 + "/loader.js")
			.headers(headers_19),
            http("request_20")
			.get(uri3 + "/css/core/tooltip.css")
			.headers(headers_3),
            http("request_21")
			.get(uri3 + "/css/util/util.css")
			.headers(headers_3),
            http("request_22")
			.get(uri3 + "/js/jsapi_compiled_format_module.js")
			.headers(headers_19),
            http("request_23")
			.get(uri3 + "/js/jsapi_compiled_default_module.js")
			.headers(headers_19),
            http("request_24")
			.get(uri3 + "/js/jsapi_compiled_corechart_module.js")
			.headers(headers_19),
            http("request_25")
			.get(uri3 + "/js/jsapi_compiled_ui_module.js")
			.headers(headers_19)))

	setUp(scn.inject(rampUsers(800) during (10 seconds))).protocols(httpProtocol)
}

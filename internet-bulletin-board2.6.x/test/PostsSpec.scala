import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.mvc._
import play.api.inject.guice._
import play.api.routing._
import play.api.routing.sird._

class PostsSpec extends PlaySpec with GuiceOneServerPerTest with OneBrowserPerSuite with HtmlUnitFactory {

  import org.openqa.selenium.htmlunit.HtmlUnitDriver

  override def createWebDriver() = {
    val driver = new HtmlUnitDriver {
      def setAcceptLanguage(lang: String) =
        this.getWebClient().addRequestHeader("Accept-Language", lang)
    }
    driver.setAcceptLanguage("en")
    driver
  }

  "GET /" should {
    "何も投稿しない場合はメッセージを表示しない" in {
      go to s"http://localhost:$port/posts/"
      assert(pageTitle === "Scala Text Textboard")
      assert(findAll(className("post-text")).length === 0)
    }
  }

  "POST /" should {
    "投稿したものが表示される" in {
      val body = "test post"

//      /posts にリダイレクト
      go to s"http://localhost:$port/posts/"
      textField(cssSelector("input#post")).value = body
      submit()

      eventually {
        val posts = findAll(className("post-text")).toSeq
        assert(posts.length === 1)
        assert(posts(0).text === body)
        assert(findAll(cssSelector("p#error")).length === 0)
      }
    }
  }

  "POST /" should {
    "投稿したものが表示される その2" in {
      go to s"http://localhost:$port/posts/"

      eventually {
        val posts = findAll(className("post-text")).toSeq
        assert(posts.length === 1)
      }
    }

    "空のメッセージは投稿できない" in {
      val body = ""

      go to s"http://localhost:$port/posts/"
      textField(cssSelector("input#post")).value = body
      submit()

      eventually {
        val error = findAll(cssSelector("p#error")).toSeq
        assert(error.length === 1)
        assert(error(0).text === "Please enter a message.")
      }
    }

    "長すぎるメッセージは投稿できない" in {
      val body = "too long messages"

      go to s"http://localhost:$port/posts/"
      textField(cssSelector("input#post")).value = body
      submit()

      eventually {
        val error = findAll(cssSelector("p#error")).toSeq
        assert(error.length === 1)
        assert(error(0).text === "The message is too long.")
      }
    }
  }
}
package client

import scala.scalajs.js.JSApp
import scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import org.scalajs.dom
import rx._
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery
import dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import model.Book
import upickle.default._

import scala.concurrent.Future

import client.utils.framework.Framework._

//import client.ChatComponents

@JSExportTopLevel("SampleApp")
@JSExportAll
object SampleApp /*extends JSApp*/ {

  def main(container: dom.html.Div): Unit = {

    import rx.Ctx.Owner.Unsafe._

    //val chatClient = new ChatClient()

    val books: Var[Seq[Book]] = Var(Seq.empty)

    val bookFragments = Rx {
      for (book <- books())
        yield li(
          s"""${book.title} - ${book.author.mkString(", ")}"""
        )
    }

    val getBooksButton = button("Get Books").render
    getBooksButton.onclick = (e: dom.MouseEvent) => {
      getBooks().foreach { newBooks =>
        books() = newBooks
      }
    }

    val emptyBooksButton = button("Empty Books").render
    emptyBooksButton.onclick = (e: dom.MouseEvent) => {
      books() = Seq.empty
    }

    val bookContainer =
      div(
        getBooksButton,
        emptyBooksButton,
        ul(
          bookFragments
        )
      )

    container.appendChild(
      bookContainer.render
    )

    container.appendChild(
      ChatComponents.buildChatContainer()
    )

  }

  def getBooks(): Future[Seq[Book]] = {
    Ajax.post("/books").map { xhr =>
      read[Seq[Book]](xhr.responseText)
    }
  }

}

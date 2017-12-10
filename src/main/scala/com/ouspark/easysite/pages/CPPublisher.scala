package com.ouspark.easysite
package pages

import com.ouspark.easysite.components.Card
import com.ouspark.easysite.components.Table.genTable
import com.ouspark.easysite.routes.{Space, SpaceRoute}
import com.ouspark.easysite.services.Api
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Vars}
import com.thoughtworks.binding.{Binding, FutureBinding, dom}
import org.scalajs.dom.raw.Node

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

class CPPublisher(pType: Option[String], taskName: Option[String], feature: Option[String]) extends Space {
  override def name: String = {
    if(pType.nonEmpty && taskName.nonEmpty && feature.nonEmpty) s"publisher/${pType.get}/${taskName.get}/${feature.get}"
    else "publisher"
  }

  val cardList = Vars(Card(1, "Standard Tasks", "standard"), Card(2, "Export Tasks", "export"), Card(3, "Import Tasks", "import"), Card(4, "Delete Tasks", "delete"))


  @dom
  override def render: Binding[BindingSeq[Node]] = {

    val mainClass = if(pType.nonEmpty && taskName.nonEmpty) "col-sm-9 ml-sm-auto col-md-10 pt-3" else "col-sm-9 col-md-10 offset-md-1"
    <div class="row">

       { navDiv(pType, taskName).bind }

      <main data:role="main" class={ mainClass }>
        { mainDetail(pType, taskName).bind }
      </main>
    </div>
    <!-- -->
  }


  @dom
  def mainDetail(pType: Option[String], taskName: Option[String]): Binding[BindingSeq[Node]] = {
    if(SpaceRoute.pages.bind.name.endsWith("publisher")) {
      <h1>Data Manager</h1>
      <div class="table-responsive">
        <div id="accordion" data:role="tablist">
          {
          for(card <- cardList) yield {
            Card.cards(card).bind
          }
          }
        </div>
      </div>
    } else if(!SpaceRoute.pages.bind.name.endsWith("summary")) {
      <h1>{ feature.get }</h1>
      <div>
        { genTable(Api.get("conf/data/table.json"), Api.get("conf/data/exp-cap-data.json")).bind }

      </div>
    }
    else {
      <h1>{ feature.get }</h1>
      <!-- -->
    }
  }


  @dom
  def navDiv(pType: Option[String], taskName: Option[String]): Binding[BindingSeq[Node]] = {
    if(pType.nonEmpty && taskName.nonEmpty) {
      <nav class="col-sm-3 col-md-2 d-none d-sm-block bg-light sidebar">
        <ul class="nav nav-pills flex-column">
          {

            FutureBinding(Api.getFeatures(pType.get)).bind match {
              case None =>
                <div>Loading</div>
              case Some(Success(resultList)) =>
                <div>{
                  Constants(resultList: _*).map { r =>
                    val className = if (SpaceRoute.pages.bind.name.endsWith(r.name.toString())) "nav-link active" else "nav-link"
                    <li class="nav-item">
                      <a href={ s"#publisher/${pType.get}/${taskName.get}/${r.name.toString}" } class={className}>{r.label.toString}</a>
                    </li>
                    }
                  }
                </div>
              case Some(Failure(exception)) =>
                <div>{ exception.toString }</div>
            }

          }
        </ul>
      </nav>
      <!-- -->
    }
    else {
      <!-- -->
      <!-- -->
    }
  }
}
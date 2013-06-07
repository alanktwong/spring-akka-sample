package org.springframework.samples.travel
package search

import org.springframework.stereotype.Service
import javax.inject.Singleton
import javax.persistence.EntityManager
import akka.actor._
import scala.collection.JavaConverters._
import javax.annotation.PostConstruct
import akka.dispatch.Await
import javax.annotation.PreDestroy
import collection.JavaConverters._
import akka.util.Timeout
import akka.util.duration._
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.injectimport org.springframework.samples.travel.search.HotelResponse
import org.springframework.samples.travel.search.HotelQuery
import org.springframework.samples.travel.search.QueryCacheActor
import org.springframework.samples.travel.search.FailWhaleActor
import org.springframework.samples.travel.search.CountryCategoryActor
.Singleton
import org.springframework.samples.travel.Hotel
import org.springframework.samples.travel.SearchCriteria
import org.springframework.samples.travel.SearchService
import org.springframework.stereotype.Service

@Service
@Singleton
class AkkaSearchBean extends SearchService  {
  /** The entity manager for this bean. */
  @(PersistenceContext @annotation.target.setter)
  var em: EntityManager = null
  
  /** The ActorSystem that runs under this bean. */
  val system =  ActorSystem("search-service")
  
  /** Returns the current search service front-end actor. */
  def searchActor: ActorRef = system actorFor (system / "search-service-frontend")
  
  @PostConstruct
  def makeSearchActor = {
    // Startup....
    def getHotels = {
      val hotels = em.createQuery("select h from Hotel h").getResultList.asInstanceOf[java.util.List[Hotel]].asScala
      hotels foreach em.detach
      hotels
    }
    // Now feed data into Akka Search service.
    //val searchProps = Props(new SingleActorSearch(getHotels))
    //val router = RoundRobinRouter(nrOfInstances=5)    
    //val rawService = system.actorOf(searchProps withRouter router, "search-service-raw")
    
    val searchTreeProps = Props(new CountryCategoryActor(getHotels))
    val rawService = system.actorOf(searchTreeProps, "search-tree")
    
    val failWhaleProps = Props(new FailWhaleActor(rawService))
    val failWhale = system.actorOf(failWhaleProps, "auto-timeout-handler")
    
    val cacheProps = Props(new QueryCacheActor(failWhale, 5))
    val cached = system.actorOf(cacheProps, "search-service-frontend")
    ()
  }
  
  @PreDestroy
  def shutdown(): Unit = system.shutdown()
  
  override def findHotels(criteria: SearchCriteria): java.util.List[Hotel] = {
    implicit val timeout = Timeout(5 seconds)
    val response = (searchActor ? HotelQuery(criteria))
    Await.result(response, akka.util.Duration.Inf) match {
      case response: HotelResponse => response.hotels.asJava
      case ex: Exception => throw ex
    }
  }
}


/**
 * Copyright © 2014 - 2020 Lightbend, Inc. All rights reserved. [http://www.lightbend.com]
 */

package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

object CoffeeHouse {

  case class CreateGuest(favoriteCoffee: Coffee)

  def props: Props =
    Props(new CoffeeHouse)
}

class CoffeeHouse extends Actor with ActorLogging {

  import CoffeeHouse._

  private val barista = createBarista()
  private val waiter = createWaiter()

  log.debug("CoffeeHouse Open")

  private val finishCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.guest.finish-coffee-duration",
      TimeUnit.MILLISECONDS).millis

  private val prepareCoffeeDuration: FiniteDuration =
    context.system.settings.config.getDuration("coffee-house.barista.prepare-coffee-duration",
      TimeUnit.MILLISECONDS).millis

  override def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
  }

  protected def createBarista(): ActorRef =
    context.actorOf(Barista.props(prepareCoffeeDuration), name = "barista")

  protected def createWaiter(): ActorRef =
    context.actorOf(Waiter.props(barista), "waiter")

  protected def createGuest(favoriteCoffee: Coffee): ActorRef =
    context.actorOf(Guest.props(waiter, favoriteCoffee, finishCoffeeDuration))
}

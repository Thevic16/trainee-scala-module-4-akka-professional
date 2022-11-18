/**
 * Copyright © 2014 - 2020 Lightbend, Inc. All rights reserved. [http://www.lightbend.com]
 */

package com.lightbend.training.coffeehouse

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object CoffeeHouse {
  case class CreateGuest(favoriteCoffee: Coffee)

  def props: Props =
    Props(new CoffeeHouse)
}

class CoffeeHouse extends Actor with ActorLogging {
  import CoffeeHouse._ // To import message protocol (CreateGuest)

  log.debug("CoffeeHouse Open")

  private val waiter: ActorRef = createWaiter()

  protected def createGuest(favoriteCoffee: Coffee): ActorRef =
    context.actorOf(Guest.props(waiter, favoriteCoffee))

  protected def createWaiter(): ActorRef =
    context.actorOf(Waiter.props(), name = "waiter")

  override def receive: Receive = {
    case CreateGuest(favoriteCoffee) => createGuest(favoriteCoffee)
    //case _ => sender() ! "Coffee Brewing"
  }
}

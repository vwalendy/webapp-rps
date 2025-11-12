package apps
package rps

import cs214.webapp.*
import cs214.webapp.server.{StateMachine}
import ujson.Value

import scala.annotation.unused
import scala.util.{Random, Try}

class Logic extends StateMachine[Event, State, View]:

  val appInfo: AppInfo = AppInfo(
    id = "rps",
    name = "Rock-Paper-Scissors",
    description = "Rock-Paper-Scissors is a hand game where Rock " +
      "crushes Scissors, Scissors cuts Paper, and Paper covers Rock.",
    year = 2025
  )

  override val wire = rps.Wire
  // Feel free to tweak this value!
  private val VIEW_HANDS_PAUSE_MS = 2500

  /** Creates a new application state. */
  override def init(clients: Seq[UserId]): State =
    ???

  override def transition(state: State)(userId: UserId, event: Event): Try[Seq[Action[State]]] =
    ???

  override def project(state: State)(userId: UserId): View =
    ???

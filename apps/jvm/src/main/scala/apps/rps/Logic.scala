package apps
package rps

import cs214.webapp.*
import cs214.webapp.server.StateMachine
import cs214.webapp.Action.*
import scala.util.{Try, Success, Failure}

class Logic extends StateMachine[Event, State, View]:

  val appInfo: AppInfo = AppInfo(
    id = "rps",
    name = "Rock-Paper-Scissors",
    description = "Rock-Paper-Scissors is a hand game where Rock " +
      "crushes Scissors, Scissors cuts Paper, and Paper covers Rock.",
    year = 2025
  )

  override val wire = rps.Wire
  private val VIEW_HANDS_PAUSE_MS = 2500

  /** Creates a new application state. */
  override def init(clients: Seq[UserId]): State =
    val hands: Map[UserId, Option[Hand]] =
      clients.map(id => id -> Option.empty[Hand]).toMap

    val scores: Map[UserId, Int] =
      clients.map(id => id -> 0).toMap

    State(hands = hands, scores = scores, viewingHands = false)

  //Helper
  private def allHandsChosen(hands: Map[UserId, Option[Hand]]): Boolean =
    hands.values.forall(_.isDefined)

  //Helper de score
  private def computeScores(
      currentScores: Map[UserId, Int],
      hands: Map[UserId, Hand]
  ): Map[UserId, Int] =
    val players = hands.keys.toSeq
    players.foldLeft(currentScores) { (scoresAcc, p) =>
      val myHand = hands(p)
      val delta =
        players.filter(_ != p)
          .map(other => myHand.scoreAgainst(hands(other)))
          .sum

      scoresAcc.updated(p, scoresAcc(p) + delta)
    }

  override def transition(state: State)(userId: UserId, event: Event): Try[Seq[Action[State]]] =
     event match
      case Event.HandSelected(hand) =>

        // Impossible de jouer si on regarde les mains
        if state.viewingHands then
          Failure(IllegalMoveException("Cannot select hand while viewing hands"))

        // Joueur inconnu
        else if !state.hands.contains(userId) then
          Failure(IllegalMoveException("Unknown player"))

        else
          state.hands(userId) match
            // Joueur a déjà choisi → interdit
            case Some(_) =>
              Failure(IllegalMoveException("Player already selected a hand"))

            case None =>
              val updatedHands = state.hands.updated(userId, Some(hand))

              // Pas encore tous prêts
              if !allHandsChosen(updatedHands) then
                val newState = state.copy(hands = updatedHands)
                Success(Seq(Render(newState)))

              else
                // Tous ont joué → on calcule les scores
                val concreteHands =
                  updatedHands.collect { case (id, Some(h)) => id -> h }

                val newScores = computeScores(state.scores, concreteHands)

                val viewingState = state.copy(
                  hands = updatedHands,
                  scores = newScores,
                  viewingHands = true
                )

                // Préparation prochaine manche
                val clearedHands =
                  updatedHands.map { case (id, _) => id -> Option.empty[Hand] }

                val nextRoundState = viewingState.copy(
                  hands = clearedHands,
                  viewingHands = false
                )

                Success(
                  Seq(
                    Render(viewingState),              // montre les résultats
                    Pause(VIEW_HANDS_PAUSE_MS),        // petite pause
                    Render(nextRoundState)             // nouvelle manche
                  )
                )

  override def project(state: State)(userId: UserId): View =
    val phaseView =
      if state.viewingHands then
        val hands = state.hands.collect { case (id, Some(h)) => id -> h }
        PhaseView.ViewingHands(hands)
      else
        val ready = state.hands.map { case (id, opt) => id -> opt.isDefined }
        PhaseView.SelectingHand(ready)

    View(
      phaseView = phaseView,
      scoresView = state.scores
    )

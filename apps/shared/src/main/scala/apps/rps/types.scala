package apps.rps

import cs214.webapp.UserId
import Hand.*

type Emoji = String

/** An enum of choices for a player's hand. */
enum Hand:
  case Rock
  case Paper
  case Scissors

  def emoji: Emoji = this match
    case Rock     => "✊"
    case Paper    => "✋"
    case Scissors => "✌️"

extension (hand: Hand)
  def scoreAgainst(other: Hand): Int = (hand, other) match
    case (Rock, Scissors) | (Paper, Rock) | (Scissors, Paper) => 1
    case (Scissors, Rock) | (Rock, Paper) | (Paper, Scissors) => -1
    case _                                                    => 0

/** A view of the rock paper scissor's state for a specific client.
  *
  * The UI alternates between two views: selecting next hand and viewing the
  * results, attributing corresponding scores.
  *
  * @param phaseView
  *   A projection of the current phase of the game.
  * @param scoresView
  *   The score of each player.
  */
case class View(
    phaseView: PhaseView,
    scoresView: ScoresView
)

enum PhaseView:
  /** Players are selecting their next hand. */
  case SelectingHand(ready: Map[UserId, Boolean])

  /** Players are looking at each other's hand. */
  case ViewingHands(hands: Map[UserId, Hand])

type ScoresView = Map[UserId, Int]

enum Event:
  /** A player has chosen their hand. */
  case HandSelected(hand: Hand)

type State = Unit // Change this!

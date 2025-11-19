package apps
package rps

import cs214.webapp.*
import cs214.webapp.DecodingException

import scala.util.Try
import ujson.*

object Wire extends AppWire[Event, View]:
  import Event.*
  import View.*

//helper
  private def encodeHand(h: Hand): Value =
    // 0 = Rock, 1 = Rock, 2 = Scissors
    Num(h.ordinal)

  private def decodeHand(json: Value): Hand =
    Hand.fromOrdinal(json.num.toInt)

//helper
  private def encodePhaseView(p: PhaseView): Value =
    p match
      case PhaseView.SelectingHand(ready) =>
        // ready: Map[UserId, Boolean] -> JSON array d'objets
        val readyArr = Arr(
          ready.toSeq.map { case (userId, isReady) =>
            Obj(
              "userId" -> Str(userId.toString),
              "ready"  -> Bool(isReady)
            )
          }*
        )
        Obj(
          "tag"   -> Str("SelectingHand"),
          "ready" -> readyArr
        )

      case PhaseView.ViewingHands(hands) =>
        // hands: Map[UserId, Hand] -> JSON array d'objets
        val handsArr = Arr(
          hands.toSeq.map { case (userId, hand) =>
            Obj(
              "userId" -> Str(userId.toString),
              "hand"   -> encodeHand(hand)
            )
          }*
        )
        Obj(
          "tag"   -> Str("ViewingHands"),
          "hands" -> handsArr
        )

  private def decodePhaseView(json: Value): PhaseView =
    val obj = json.obj
    val tag = obj("tag").str
    tag match
      case "SelectingHand" =>
        val readyArr = obj("ready").arr
        val readyMap: Map[UserId, Boolean] =
          readyArr.map { v =>
            val o       = v.obj
            val userId: UserId = o("userId").str
            val ready   = o("ready").bool
            userId -> ready
          }.toMap
        PhaseView.SelectingHand(readyMap)

      case "ViewingHands" =>
        val handsArr = obj("hands").arr
        val handsMap: Map[UserId, Hand] =
          handsArr.map { v =>
            val o       = v.obj
            val userId: UserId = o("userId").str
            val hand    = decodeHand(o("hand"))
            userId -> hand
          }.toMap
        PhaseView.ViewingHands(handsMap)

      case other =>
        throw DecodingException(s"Unknown phase tag: $other")

//helper
  private def encodeScoresView(scores: ScoresView): Value =
    // scores: Map[UserId, Int] -> JSON array d'objets
    Arr(
      scores.toSeq.map { case (userId, score) =>
        Obj(
          "userId" -> Str(userId.toString),
          "score"  -> Num(score)
        )
      }*
    )

  private def decodeScoresView(json: Value): ScoresView =
    val arr = json.arr
    arr.map { v =>
      val o       = v.obj
      val userId: UserId = o("userId").str
      val score   = o("score").num.toInt
      userId -> score
    }.toMap


  override object eventFormat extends WireFormat[Event]:
    override def encode(event: Event): Value =
      event match
        case HandSelected(hand) =>
          Obj(
            "tag"  -> Str("HandSelected"),
            "hand" -> encodeHand(hand)
          )

    override def decode(json: Value): Try[Event] =
      Try {
        val obj = json.obj
        val tag = obj("tag").str

        tag match
          case "HandSelected" =>
            val handJson = obj("hand")
            val hand     = decodeHand(handJson)
            HandSelected(hand)
          case other =>
            throw DecodingException(s"Unknown event tag: $other")
      }


  override object viewFormat extends WireFormat[View]:

    override def encode(v: View): Value =
      Obj(
        "phaseView"  -> encodePhaseView(v.phaseView),
        "scoresView" -> encodeScoresView(v.scoresView)
      )

    override def decode(json: Value): Try[View] =
      Try {
        val obj        = json.obj
        val phaseJson  = obj("phaseView")
        val scoresJson = obj("scoresView")

        val phaseView  = decodePhaseView(phaseJson)
        val scoresView = decodeScoresView(scoresJson)

        View(phaseView, scoresView)
      }

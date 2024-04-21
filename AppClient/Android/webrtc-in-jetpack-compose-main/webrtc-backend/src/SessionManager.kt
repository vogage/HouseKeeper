import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

/**
 * Originally written by Artem Bagritsevich.
 *
 * https://github.com/artem-bagritsevich/WebRTCKtorSignalingServerExample
 */
object SessionManager {

    private val sessionManagerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()

    private val clients = mutableMapOf<UUID, DefaultWebSocketServerSession>()

    private var sessionState: WebRTCSessionState = WebRTCSessionState.Impossible

    //webSocket("/rtc") {
    //    val sessionID = UUID.randomUUID()
    //    try {
    //        SessionManager.onSessionStarted(sessionID, this)

    fun onSessionStarted(sessionId: UUID, session: DefaultWebSocketServerSession) {//initial function with sessionID
        sessionManagerScope.launch {
            mutex.withLock {
                if (clients.size > 1) {
                    sessionManagerScope.launch(NonCancellable) {
                        session.send(Frame.Close()) // only two peers are supported
                    }
                    return@launch
                }
                clients[sessionId] = session
                session.send("Added as a client: $sessionId") //asyn send message
                if (clients.size > 1) {
                    sessionState = WebRTCSessionState.Ready
                }
                notifyAboutStateUpdate() //update the client stage
            }
        }
    }

    //OkHttp http://192.168.12.234:6503/...:42) websocketListener onmessage: STATE Impossible
    //OkHttp http://192.168.12.234:6503/...:42) Qian received statemessage: STATE Impossible
    //OkHttp http://192.168.12.234:6503/...:42) websocketListener onmessage: Added as a client: 7643480b-3006-476b-8089-2813e5956468


    fun onMessage(sessionId: UUID, message: String) {
        when {
            message.startsWith(MessageType.STATE.toString(), true) -> handleState(sessionId)
            message.startsWith(MessageType.OFFER.toString(), true) -> handleOffer(sessionId, message)
            message.startsWith(MessageType.ANSWER.toString(), true) -> handleAnswer(sessionId, message)
            message.startsWith(MessageType.ICE.toString(), true) -> handleIce(sessionId, message)
        }
    }

    private fun handleState(sessionId: UUID) {
        sessionManagerScope.launch {
            clients[sessionId]?.send("${MessageType.STATE} $sessionState")
        }
    }

    private fun handleOffer(sessionId: UUID, message: String) {
        if (sessionState != WebRTCSessionState.Ready) {
            error("Session should be in Ready state to handle offer")
        }
        sessionState = WebRTCSessionState.Creating
        println("handling offer from $sessionId")
        notifyAboutStateUpdate()
        val clientToSendOffer = clients.filterKeys { it != sessionId }.values.first()
        println("clientToSendOffer $message")
        clientToSendOffer.send(message)
    }

    private fun handleAnswer(sessionId: UUID, message: String) {
        if (sessionState != WebRTCSessionState.Creating) {
            error("Session should be in Creating state to handle answer")
        }
        println("handling answer from $sessionId")
        val clientToSendAnswer = clients.filterKeys { it != sessionId }.values.first()
        clientToSendAnswer.send(message)
        sessionState = WebRTCSessionState.Active
        notifyAboutStateUpdate()
    }

    private fun handleIce(sessionId: UUID, message: String) {
        println("handling ice from $sessionId")
        val clientToSendIce = clients.filterKeys { it != sessionId }.values.first()
        clientToSendIce.send(message)
    }

    fun onSessionClose(sessionId: UUID) {
        sessionManagerScope.launch {
            mutex.withLock {
                clients.remove(sessionId)
                sessionState = WebRTCSessionState.Impossible
                notifyAboutStateUpdate()
            }
        }
    }

    enum class WebRTCSessionState {
        Active, // Offer and Answer messages has been sent
        Creating, // Creating session, offer has been sent
        Ready, // Both clients available and ready to initiate session
        Impossible // We have less than two clients
    }

    enum class MessageType {
        STATE,
        OFFER,
        ANSWER,
        ICE
    }

    private fun notifyAboutStateUpdate() {
        println("Qian notifyAboutStateUpate ${MessageType.STATE} $sessionState")
        clients.forEach { (_, client) ->
            client.send("${MessageType.STATE} $sessionState")
        }
    }

    private fun DefaultWebSocketServerSession.send(message: String) {
        sessionManagerScope.launch {
            this@send.send(Frame.Text(message))
        }
    }
}

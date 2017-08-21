import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.google.gson.Gson
import com.redstar.ws.WebSocketClient
import net.grinder.plugin.http.HTTPPluginControl
import net.grinder.script.GTest
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.AfterThread
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import static net.grinder.script.Grinder.grinder
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat
// import static net.grinder.util.GrinderUtils.* // You can use this if you're using nGrinder after 3.2.3
/**
 * A simple example using the HTTP plugin that shows the retrieval of a
 * single page via HTTP. 
 *
 * This script is automatically generated by ngrinder.
 *
 * @author admin
 */
@RunWith(GrinderRunner)
class TestRunner {

	static GTest test
	static WebSocketClient webSocketClient
	static WebSocketClient chatWebSocketClient
	static int curNumber
	static String playerId
	static Map<String, Object> headers
	static Gson gson

	static GAME_SERVER_ADDRESS = "ws://10.10.0.234:20371"
	static TEST_SERVER_ADDRESS = "ws://10.10.0.234:21371"
	static CHAT_SERVER_ADDRESS = "ws://10.10.0.234:20471"

	static GAME_SERVER_APPID = "myhome_game"
	static TEST_SERVER_APPID = "myhome_test"
	static CHAT_SERVER_APPID = "myhome_chat"

	// api uri prefix
	static GAME_API_URI_PREFIX = "/v1"
	static CHAT_API_URI_PREFIX = "/v1"

	// thread sleep 설정
	static DEFAULT_SLEEP_TIME = 3000
	static USE_SLEEP = false

	static GAME_SERVER = 0
	static CHAT_SERVER = 1
	static GAME_SERVER_TEST = 2

	// logger 설정
	static Logger logger
	static Level logLevel = Level.INFO
	//static Level logLevel = Level.ERROR
	
	static int userNum = 0

	@BeforeProcess
	static void beforeProcess() {
		HTTPPluginControl.getConnectionDefaults().timeout = 6000
		test = new GTest(1, "Test1")
		gson = new Gson()

		logger = (Logger) LoggerFactory.getLogger("worker")
		logger.setLevel(logLevel)

		logger.info("before process.")
	}

	@BeforeThread
	void beforeThread() {
		grinder.statistics.delayReports = true
		logger.info("before thread.")

		int totalProcessCount = grinder.getProcessNumber()
		int totalThreadCount = grinder.getThreadNumber()
		int agentNumber = grinder.agentNumber
		int processNumber = grinder.processNumber
		int threadNumber = grinder.threadNumber
		curNumber = (agentNumber * totalProcessCount * totalThreadCount) + (processNumber * totalThreadCount) + threadNumber + 1
		//playerId = "MHTestE" + Integer.toString(13000000 + curNumber)
		//logger.info("playerId : {}", playerId)

		webSocketClient = new WebSocketClient()
		webSocketClient.connect(TEST_SERVER_ADDRESS + "/" + TEST_SERVER_APPID)

		//chatWebSocketClient = new WebSocketClient()
		//chatWebSocketClient.connect(CHAT_SERVER_ADDRESS + "/" + CHAT_SERVER_APPID)

		playerId = randomTestPlayerId()

		headers = new HashMap<>()
		headers.put("txNo", getTxNo())
		headers.put("playerId", playerId)
		headers.put("sessionId", "nGrinderSession")

		// login()

		// 테스트 전 login 후 계정정보가 없으면 create 하여 계정을 준비 시킨다.
//		String loginResponse = login()
//		JSONObject response = getResponse(loginResponse)
//		logger.info("status {}", response.getInt("status"))
//		if (response.getInt("status") == 301101) {
//			threadSleep(500)
//			create()
//		}

		test.record(this, "Test")

	}

	@AfterThread
	void afterThread() {
		//logout()
		if (webSocketClient != null) {
			webSocketClient.disconnect()
		}

		if (chatWebSocketClient != null) {
			chatWebSocketClient.disconnect()
		}

		logger.info("after thread.")
	}

	String login() {
		Map<String, Object> body = new HashMap<>()
		body.put("gameVer", "1111")
		body.put("os", "1")
		body.put("osVer", "1")
		body.put("deviceToken", "1")
		body.put("deviceId", "nGrinder")

		String response = webSocketClient.syncSendMessage(makePacket("/v1/session/login2", body, headers))
		logger.info("login response : {}", response)

		return response
	}

	String create() {
		Map<String, Object> body = new HashMap<>()
		body.put("nickname", playerId)
		body.put("characterId", 100)
		body.put("gender", 0)
		body.put("eyeColorId", 21001)
		body.put("hairSkinId", 21112)
		body.put("hairColorId", 21201)
		body.put("clothSkinId", 21301)
		body.put("bodyColorId", 21401)
		body.put("faceSkinId", 21501)
		body.put("starTree", 3001)
		body.put("kakaoUser", false)

		String response = webSocketClient.syncSendMessage(makePacket("/v1/user/create", body, null))
		logger.info("create response : {}", response)

		return response
	}

	String logout() {
		Map<String, Object> body = new HashMap<>()
		body.put("appId", "myhome")
		body.put("playerId", playerId)

		String response = webSocketClient.syncSendMessage(makePacket("/v1/session/logout", body, null))
		logger.info("logout response : {}", response)

		return response
	}

	JSONObject getResponse(String response) {
		JSONArray apiResult = new JSONArray(response)
		return (JSONObject) apiResult.get(2)
	}

	// [{}, {}, {}] WebSocket 패킷을 만든다.
	String makePacket(String api, Map<String, Object> body, Map<String, Object> headers) {
		if (headers == null) {
			headers = this.headers
		}

		String packet = "[\"" + api + "\","
		packet += gson.toJson(headers) + ","

		packet += gson.toJson(body) + "]"
		logger.info("api : {}, body {}", api, packet)

		return packet
	}

	void threadSleep(Integer sleepTime) {
		if (USE_SLEEP) {
			if (sleepTime == null) {
				sleepTime = DEFAULT_SLEEP_TIME
			}
			grinder.sleep(sleepTime)
		}
	}

	String randomTestPlayerId() {
		Random rnd = new Random()
		int p = rnd.nextInt(300) + 1
		return "MHTestE1100" + String.format("%03d", p)
	}

	int getTxNo() {
		return new Random().nextInt()
	}
	
	int makeDataLoopCnt() {
		return 65
	}

	@Test
	void memcachedGetTest() {
		headers = new HashMap<>()
		headers.put("txNo", getTxNo())
		headers.put("playerId", randomTestPlayerId())
		headers.put("makeDataLoopCnt", makeDataLoopCnt())
		headers.put("sessionId", "nGrinderSession")
		Map<String, Object> body = new HashMap<>()

		String response = webSocketClient.syncSendMessage(makePacket("/v1/test/cache/external/userFieldData/get", body, headers))
		logger.info("memcachedTest response {}", response)
		assertThat(getResponse(response).getInt("status"), is(200))

		threadSleep(null)
	}

	@Test
	void memcachedSetTest() {
		headers = new HashMap<>()
		headers.put("txNo", getTxNo())
		headers.put("playerId", randomTestPlayerId())
		headers.put("makeDataLoopCnt", makeDataLoopCnt())
		headers.put("sessionId", "nGrinderSession")

		Map<String, Object> body = new HashMap<>()

		String response = webSocketClient.syncSendMessage(makePacket("/v1/test/cache/external/userFieldData/set", body, headers))
		logger.info("memcachedTest response {}", response)
		assertThat(getResponse(response).getInt("status"), is(200))

		threadSleep(null)
	}

	void memcachedGetAndSetTest() {
		headers = new HashMap<>()
		headers.put("txNo", getTxNo())
		headers.put("playerId", randomTestPlayerId())
		headers.put("makeDataLoopCnt", makeDataLoopCnt())
		headers.put("sessionId", "nGrinderSession")

		Map<String, Object> body = new HashMap<>()

		String response = webSocketClient.syncSendMessage(makePacket("/v1/test/cache/external/getAndSet", body, headers))
		logger.info("memcachedTest response {}", response)
		assertThat(getResponse(response).getInt("status"), is(200))

		threadSleep(null)
	}
}

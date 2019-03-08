package com.fantasystep.persistence.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fantasystep.domain.User;
import com.fantasystep.persistence.exception.InvalidSessionException;
import com.fantasystep.utils.Option;
import com.fantasystep.utils.Option.FileOption;
import com.fantasystep.utils.Option.InvalidOptionFormatException;

@Singleton
@Startup
public class SessionManager {
	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	static {
		try {
			Option.setConfigFileName("/etc/fantasystep/sessionmanager.conf");
			FileOption SESSIONS_FILE = new FileOption("sessions.file",
					new File("/etc/fantasystep/sessions.conf"), true,
					"File to store sessions while offline");
			Option.load();
			setSessionsFile(SESSIONS_FILE.value());
		} catch (InvalidOptionFormatException e) {
			e.printStackTrace();
		}
	}

	private static File sessionsFile;

	private static File getSessionsFile() {
		return sessionsFile;
	}

	private static void setSessionsFile(File sessionsFile) {
		SessionManager.sessionsFile = sessionsFile;
	}

	private Map<UUID, Session> sessions;

	public void cleanUp() {
		synchronized (getSessions()) {
			long time = 86400000; // 24 hours in ms
			Date limit = new Date(new Date().getTime() - time);

			List<UUID> expiredSessions = new ArrayList<UUID>();

			for (Entry<UUID, Session> entry : getSessions().entrySet())
				if (entry.getValue().getCreatedAt().before(limit))
					expiredSessions.add(entry.getKey());

			for (UUID sessionKey : expiredSessions)
				getSessions().remove(sessionKey);
		}
	}

	public UUID createSession(User user) {
		// One to one session
		if (!getSessions(user).isEmpty())
			return getSessions(user).get(0);
		
		Session session = new Session(user);
		getSessions().put(session.getKey(), session);

		logger.info(String.format("Inserting session %s for user %s",
				session.getKey().toString(), user.getLabel()));

		return session.getKey();
	}

	public void destroySesisons(User user) {
		for (UUID sessionKey : getSessions(user))
			getSessions().remove(sessionKey);
	}

	public void destroySession(UUID sessionKey) throws InvalidSessionException {
		if (!getSessions().containsKey(sessionKey))
			throw new InvalidSessionException(String.format(
					"Invalid session: %s", sessionKey));
		getSessions().remove(sessionKey);
	}

	public void destroyUsersOtherSessions(UUID sessionKey)
			throws InvalidSessionException {
		User user = getUser(sessionKey);
		List<UUID> userSessions = getSessions(user);
		userSessions.remove(sessionKey);
		for (UUID key : userSessions) {
			logger.info("Removing session: " + key);
			destroySession(key);
		}

	}

	private Map<UUID, Session> getSessions() {
		return sessions;
	}

	private List<UUID> getSessions(User user) {
		List<UUID> userSessions = new ArrayList<UUID>();
		for (Entry<UUID, Session> entry : getSessions().entrySet())
			if (entry.getValue().getUser().equals(user))
				userSessions.add(entry.getKey());

		return userSessions;
	}

	public User getUser(UUID sessionKey) throws InvalidSessionException {
		if (!isSessionValid(sessionKey))
			throw new InvalidSessionException(String.format(
					"Invalid session: %s", sessionKey));

		return getSessions().get(sessionKey).getUser();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		logger.info("Init in SessionManager");
		ObjectInputStream objIn = null;
		try {
			if (getSessionsFile().exists()) {
				FileInputStream fis = new FileInputStream(getSessionsFile());
				objIn = new ObjectInputStream(fis);
				setSessions((HashMap<UUID, Session>) objIn.readObject());
				if (getSessions() == null) {
					logger.info("First time to read serialization file. File is empty. Init Sessions HashMap ...");
					setSessions(new HashMap<UUID, Session>());
				}
			} else setSessions(new HashMap<UUID, Session>());
			logger.info("SessionManager initialization done.");
		} catch (java.io.EOFException e) {
			logger.info("No backup session files.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objIn != null)
					objIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@PreDestroy
	public void saveSessions() {
		ObjectOutputStream objOut = null;
		try {
			FileOutputStream fos = new FileOutputStream(getSessionsFile());
			objOut = new ObjectOutputStream(fos);
			objOut.writeObject(sessions);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objOut != null)
					objOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isSessionValid(UUID sessionKey) {
		return getSessions().containsKey(sessionKey);
	}

	private void setSessions(Map<UUID, Session> sessions) {
		this.sessions = sessions;
	}
}

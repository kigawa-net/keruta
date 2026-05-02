package net.kigawa.keruta.kise.usecase.session

import net.kigawa.keruta.kise.domain.entity.Session
import net.kigawa.keruta.kise.domain.error.KiseErr
import net.kigawa.keruta.kise.domain.error.SessionExpiredErr
import net.kigawa.keruta.kise.domain.error.SessionNotFoundErr
import net.kigawa.keruta.kise.domain.repository.SessionRepository
import net.kigawa.kodel.api.err.Res

/**
 * セッション検証ユースケース
 */
class VerifySessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    /**
     * トークンからセッションを検証する
     */
    suspend fun execute(token: String): Res<Session, KiseErr> {
        val session = sessionRepository.getByToken(token)
            ?: return Res.Err(SessionNotFoundErr("Session not found"))

        val currentTime = System.currentTimeMillis()
        if (session.isExpired(currentTime)) {
            sessionRepository.deleteByToken(token)
            return Res.Err(SessionExpiredErr("Session has expired"))
        }

        return Res.Ok(session)
    }
}

/**
 * セッション更新ユースケース
 */
class RefreshSessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    /**
     * セッションを更新する（有効期限を延長）
     */
    suspend fun execute(token: String): Res<Session, KiseErr> {
        val session = sessionRepository.getByToken(token)
            ?: return Res.Err(SessionNotFoundErr("Session not found"))

        val currentTime = System.currentTimeMillis()
        if (session.isExpired(currentTime)) {
            sessionRepository.deleteByToken(token)
            return Res.Err(SessionExpiredErr("Session has expired"))
        }

        // 有効期限を延長（元の有効期限から1時間）
        val newExpiresAt = maxOf(
            session.expiresAt,
            currentTime
        ) + 3_600_000

        val updatedSession = session.copy(
            expiresAt = newExpiresAt,
            updatedAt = currentTime,
        )

        // 更新（簡易実装）
        return Res.Ok(updatedSession)
    }
}

/**
 * ログアウトユースケース
 */
class LogoutUseCase(
    private val sessionRepository: SessionRepository,
) {
    /**
     * セッションを削除（ログアウト）
     */
    suspend fun execute(token: String): Res<Unit, KiseErr> {
        sessionRepository.deleteByToken(token)
        return Res.Ok(Unit)
    }
}
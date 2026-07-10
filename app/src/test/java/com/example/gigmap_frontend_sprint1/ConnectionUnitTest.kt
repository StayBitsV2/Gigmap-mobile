package com.example.gigmap_frontend_sprint1

import com.example.gigmap_frontend_sprint1.model.ConnectionRequestResource
import com.example.gigmap_frontend_sprint1.model.ConnectionResource
import com.example.gigmap_frontend_sprint1.model.CreateConnectionRequest
import org.junit.Test
import org.junit.Assert.*

class ConnectionUnitTest {

    @Test
    fun testConnectionRequestResourceCreation() {
        val request = ConnectionRequestResource(
            id = 1L,
            requesterId = 10L,
            targetId = 20L,
            status = "PENDING",
            createdAt = "2026-07-09T10:00:00"
        )
        assertEquals(1L, request.id)
        assertEquals(10L, request.requesterId)
        assertEquals(20L, request.targetId)
        assertEquals("PENDING", request.status)
        assertNotNull(request.createdAt)
    }

    @Test
    fun testConnectionResourceCreation() {
        val connection = ConnectionResource(
            id = 1L,
            connectedUserId = 20L,
            connectedUsername = "fan_juan",
            connectedUserImage = "http://example.com/img.jpg",
            createdAt = "2026-07-09T10:00:00"
        )
        assertEquals(20L, connection.connectedUserId)
        assertEquals("fan_juan", connection.connectedUsername)
        assertNotNull(connection.connectedUserImage)
    }

    @Test
    fun testCreateConnectionRequest() {
        val request = CreateConnectionRequest(targetId = 20L)
        assertEquals(20L, request.targetId)
    }

    @Test
    fun testConnectionRequestStatusValues() {
        val pending = ConnectionRequestResource(1L, 1L, 2L, "PENDING", null)
        val accepted = ConnectionRequestResource(2L, 1L, 2L, "ACCEPTED", null)
        val rejected = ConnectionRequestResource(3L, 1L, 2L, "REJECTED", null)

        assertEquals("PENDING", pending.status)
        assertEquals("ACCEPTED", accepted.status)
        assertEquals("REJECTED", rejected.status)
    }

    @Test
    fun testConnectionWithNullUsernameAndImage() {
        val connection = ConnectionResource(
            id = 1L,
            connectedUserId = 20L,
            connectedUsername = null,
            connectedUserImage = null,
            createdAt = null
        )
        assertNull(connection.connectedUsername)
        assertNull(connection.connectedUserImage)
        assertNull(connection.createdAt)
    }
}

package com.example.halp

import com.google.firebase.Timestamp
import org.junit.Test

import org.junit.Assert.*
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun ordersAdapter_correct_getItemCount(){
        val sampleOrder = Order()
        val activity = MainActivity()
        val adapter = OrdersAdapter(act = activity, orders = arrayListOf())

        assertEquals(adapter.getItemCount(), 0)

        adapter.orders?.plusAssign(sampleOrder)

        assertEquals(adapter.getItemCount(), 1)

        for (i in 1..35)
            adapter.orders?.plusAssign(sampleOrder)

        assertEquals(adapter.getItemCount(), 36)

        for (i in 1..33)
            adapter.orders?.plusAssign(sampleOrder)

        assertEquals(adapter.getItemCount(), 69)
    }

    @Test
    fun questListAdapter_correct_getItemCount(){
        val sampleQuest = Quest()
        val activity = MainActivity()
        val adapter = QuestListAdapter(act = activity, quests = arrayListOf())

        assertEquals(adapter.getItemCount(), 0)

        adapter.quests.plusAssign(sampleQuest)

        assertEquals(adapter.getItemCount(), 1)

        for (i in 1..35)
            adapter.quests.plusAssign(sampleQuest)

        assertEquals(adapter.getItemCount(), 36)

        for (i in 1..33)
            adapter.quests.plusAssign(sampleQuest)

        assertEquals(adapter.getItemCount(), 69)
    }

    @Test
    fun order_correct_set(){
        val order = Order()
        val quest_id = "12345"
        val quest_date = Timestamp(Date())
        val order_date = Timestamp(Date())
        val people = 5
        val cost = 1325555
        val status = "CoolStatus18"
        val comment = ""

        order.set("quest_id", quest_id)
        order.set("quest_date", quest_date)
        order.set("order_date", order_date)
        order.set("people", people)
        order.set("cost", cost)
        order.set("status", status)
        order.set("comment", comment)

        assertEquals(order.quest_id, quest_id)
        assertEquals(order.quest_date, quest_date.toDate())
        assertEquals(order.order_date, order_date.toDate())
        assertEquals(order.people, people)
        assertEquals(order.cost, cost)
        assertEquals(order.status, status)
        assertEquals(order.comment, comment)

    }
}

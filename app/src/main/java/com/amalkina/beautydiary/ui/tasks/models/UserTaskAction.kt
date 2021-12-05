package com.amalkina.beautydiary.ui.tasks.models

sealed class UserTaskAction {
    class OnClickTask(val id: Long) : UserTaskAction()
    class AddTask(val categoryId: Long) : UserTaskAction()
    class EditTask(val id: Long, val categoryId: Long) : UserTaskAction()
    class DeleteTask(val id: Long, val name: String) : UserTaskAction()
}
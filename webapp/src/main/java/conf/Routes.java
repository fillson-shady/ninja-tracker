/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package conf;

import controllers.CustomAssetsController;
import controllers.LoginController;
import controllers.TodoController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {  
        
        //router.GET().route("/").with(ApplicationController.class, "index");
        router.GET().route("/userProfile").with(ApplicationController.class, "userProfile");
        router.POST().route("/userLink").with(ApplicationController.class, "createUserLinkPost");
        router.GET().route("/ping").with(ApplicationController.class, "ping");

        router.GET().route("/login").with(LoginController.class, "login");
        router.GET().route("/logout").with(LoginController.class, "logout");
        router.POST().route("/login").with(LoginController.class, "loginPost");
        router.GET().route("/checkFreeEmail").with(LoginController.class, "checkFreeEmail");

        router.GET().route("/expenses").with(ApplicationController.class, "index");

        router.GET().route("/todo").with(TodoController.class, "todoList");
        router.GET().route("/todo/list").with(TodoController.class, "todoListPost");
        router.POST().route("/todo/add").with(TodoController.class, "addTodoPost");
        router.POST().route("/todo/edit/{id}").with(TodoController.class, "editTodoPost");
        router.POST().route("/todo/complete/{id}").with(TodoController.class, "completeTodoPost");
        router.POST().route("/todo/remove/{id}").with(TodoController.class, "removeTodoPost");
        router.POST().route("/todo/completeAll").with(TodoController.class, "completeAllTodoPost");
        router.POST().route("/todo/removeCompleted").with(TodoController.class, "removeCompletedTodoPost");

        router.GET().route("/assets/webjars/{fileName: .*}").with(CustomAssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(CustomAssetsController.class, "serveStatic");

        router.GET().route("/.*").with(TodoController.class, "todoList");
    }
}

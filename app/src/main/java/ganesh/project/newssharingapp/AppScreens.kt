package ganesh.project.newssharingapp

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_route")
    object Login : AppScreens("login_route")
    object Register : AppScreens("register_route")

    object Home : AppScreens("home_screen")
    object CreatePost : AppScreens("create_post")
    object MyPosts : AppScreens("my_posts")
    object AllPosts : AppScreens("all_posts")
    object SavedPosts : AppScreens("saved_posts")

}
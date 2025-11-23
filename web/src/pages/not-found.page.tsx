import { Link } from "@tanstack/react-router"
import { Button } from "@/design-system/components/ui/button"
import { Home } from "@/components/icons"

export const NotFoundPage = () => {
    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center py-12">
            <div className="space-y-6">
                <h1 className="text-9xl font-bold">404</h1>
                <div className="space-y-2">
                    <h2 className="text-3xl font-semibold">Page not found</h2>
                    <p className="text-muted-foreground text-lg max-w-md">
                        Sorry, we couldn't find the page you're looking for. It might have been moved or deleted.
                    </p>
                </div>
                <Button size="lg" asChild className="mt-8">
                    <Link to="/">
                        <Home className="mr-2 h-5 w-5" />
                        Back to Home
                    </Link>
                </Button>
            </div>
        </div>
    )
}

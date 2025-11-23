import { Main } from "@/components/main"
import { Button } from "@/design-system/components/ui/button"
import { Input } from "@/design-system/components/ui/input"
import { Outlet, useNavigate, useRouter } from "@tanstack/react-router"
import { ChevronLeft } from "@/components/icons"
import { useAppDispatch } from "@/hooks/store-hooks"
import { selectSearchQuery, setSearchQuery } from "@/core/search/search.slice"
import { searchEvents } from "@/core/search/usecases/search-events/search-events.usecase"
import { useSelector } from "react-redux"

export const SearchLayout = () => {
    const navigate = useNavigate()
    const router = useRouter()
    const dispatch = useAppDispatch()
    const query = useSelector(selectSearchQuery)

    const handleBack = () => {
        if (router.history.canGoBack()) {
            router.history.back()
        } else {
            navigate({ to: "/" })
        }
    }

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const query = e.target.value
        dispatch(setSearchQuery(query))
        dispatch(searchEvents())
    }

    return (
        <div className="min-h-screen flex flex-col">
            <header className="w-full py-4">
                <div className="container mx-auto px-4">
                    <div className="relative flex items-center" style={{ viewTransitionName: 'search-input' }}
                    >
                        <Button
                            className="absolute left-2 rounded-full z-10"
                            variant="ghost"
                            onClick={handleBack}
                        >
                            <ChevronLeft className="h-6 w-6" />
                        </Button>
                        <Input
                            type="search"
                            placeholder="Search for events"
                            autoFocus
                            className="pl-14 py-6"
                            value={query}
                            onChange={handleSearchChange}
                        />
                    </div>
                </div>
            </header>
            <Main>
                <Outlet />
            </Main>
        </div>
    )
}

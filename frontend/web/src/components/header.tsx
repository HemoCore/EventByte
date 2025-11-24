import { Search, LogOut, Ticket } from "@/components/icons"
import { Link, useNavigate } from "@tanstack/react-router"
import { Button } from "@/design-system/components/ui/button"
import { Avatar, AvatarFallback } from "@/design-system/components/ui/avatar"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/design-system/components/ui/dropdown-menu"
import logo from "@/assets/logo.png"
import { useAppDispatch } from "@/hooks/store-hooks"
import { login } from "@/core/auth/usecases/login/login.usecase"
import { logout } from "@/core/auth/usecases/logout/logout.usecase"
import { useSelector } from "react-redux"
import { isAuthSelector, authSelector, isCheckingAuthSelector } from "@/core/auth/auth.slice"
import { Input } from "@/design-system/components/ui/input"

export const Header = () => {
    const dispatch = useAppDispatch()
    const navigate = useNavigate()
    const isCheckingAuth = useSelector(isCheckingAuthSelector)
    const isAuthenticated = useSelector(isAuthSelector)
    const auth = useSelector(authSelector)

    const handleLogin = () => {
        dispatch(login())
    }

    const handleLogout = () => {
        dispatch(logout())
    }

    const getInitials = (fullname: string) => {
        return fullname
            .split(' ')
            .map(n => n[0])
            .join('')
            .toUpperCase()
            .slice(0, 2)
    }

    return (
        <header className="w-full">
            <div className="container mx-auto flex h-16 items-center gap-4 px-4">
                <Link to="/">
                    <img src={logo} alt="Logo" className="h-10 w-auto" />
                </Link>

                <div className="flex-1 max-w-2xl" >
                    <div className="relative cursor-pointer" style={{ viewTransitionName: 'search-input' }} onClick={() => navigate({ to: '/search' })}
                    >
                        <div className="absolute left-2 top-1/2 -translate-y-1/2 z-10 pointer-events-none" >
                            <Search className="h-4 w-4 text-muted-foreground" />
                        </div>
                        <Input
                            type="search"
                            placeholder="Search for events"
                            disabled
                            className="pl-10 py-3 focus-visible:border-input focus-visible:ring-0 disabled:opacity-100"
                        />
                    </div>
                </div>

                <div className="items-center gap-4 ml-auto flex">
                    <Button variant="link" className="hidden sm:flex" asChild>
                        <a href="http://localhost:1337" target="_blank">
                            I'm an organizer
                        </a>
                    </Button>

                    {isCheckingAuth ? (
                        <Skeleton className="h-9 w-32" />
                    ) : isAuthenticated && auth.user ? (
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                                    <Avatar>
                                        <AvatarFallback>{getInitials(auth.user.fullname)}</AvatarFallback>
                                    </Avatar>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                                <DropdownMenuLabel>
                                    <div className="flex flex-col space-y-1">
                                        <p className="text-sm font-medium">{auth.user.fullname}</p>
                                        <p className="text-xs text-muted-foreground">{auth.user.email}</p>
                                    </div>
                                </DropdownMenuLabel>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem asChild>
                                    <Link to="/tickets">
                                        <Ticket className="mr-2 h-4 w-4" />
                                        Tickets
                                    </Link>
                                </DropdownMenuItem>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={handleLogout}>
                                    <LogOut className="mr-2 h-4 w-4" />
                                    Logout
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    ) : (
                        <Button onClick={handleLogin}>Login / Sign up</Button>
                    )}
                </div>
            </div>
        </header>
    )
}

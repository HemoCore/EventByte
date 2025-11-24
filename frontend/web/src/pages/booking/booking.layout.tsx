import { Main } from "@/components/main";
import { Link, Outlet } from "@tanstack/react-router";
import { FC } from "react";
import logo from "@/assets/logo.png"
import { Support } from "@/components/icons";
import { Button } from "@/design-system/components/ui/button";
import { BookingTimer } from "./components/booking-timer";


export const BookingLayout: FC = () => {
    return (
        <div className="min-h-screen flex flex-col">
            <header className="w-full py-3 border-b">
                <div className="container mx-auto flex items-center gap-4 px-4 justify-between">
                    <Link to="/">
                        <img src={logo} alt="Logo" className="h-10 w-auto" />
                    </Link>
                    <div className="flex items-center gap-2">
                        <BookingTimer />
                        <Button variant="ghost" size="icon-lg">
                            <Support />
                        </Button>
                    </div>
                </div>
            </header >
            <Main className="space-y-4 max-w-3xl p-4 mx-auto">
                <Outlet />
            </Main>
        </div >
    )
}

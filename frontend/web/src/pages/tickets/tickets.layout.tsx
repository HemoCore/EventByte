import { Outlet } from "@tanstack/react-router"
import { Header } from "@/components/header"
import { Footer } from "@/components/footer"
import { Main } from "@/components/main"

export const TicketsLayout = () => {
	return (
		<div className="min-h-screen flex flex-col">
			<Header />
			<Main>
				<Outlet />
			</Main>
			<Footer />
		</div>
	)
}

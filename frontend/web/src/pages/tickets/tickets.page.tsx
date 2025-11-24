import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/design-system/components/ui/tabs"
import { UpComingTickets } from "./components/upcoming-tickets"
import { PastTickets } from "./components/past-tickets"

export const TicketsPage = () => {
    return (
        <div className="py-8 space-y-8">
            <div className="space-y-2">
                <h1 className="text-3xl font-bold tracking-tight">Tickets</h1>
                <p className="text-muted-foreground">
                    View and manage your event reservations
                </p>
            </div>

            <Tabs defaultValue="upcoming" className="w-full">
                <TabsList className="grid w-full max-w-md grid-cols-2">
                    <TabsTrigger value="upcoming">Upcoming</TabsTrigger>
                    <TabsTrigger value="past">Past</TabsTrigger>
                </TabsList>

                <TabsContent value="upcoming" className="mt-6">
                    <UpComingTickets />
                </TabsContent>

                <TabsContent value="past" className="mt-6">
                    <PastTickets />
                </TabsContent>
            </Tabs>
        </div>
    )
}

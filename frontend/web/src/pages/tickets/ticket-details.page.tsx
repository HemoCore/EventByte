import { Link, notFound, useParams } from "@tanstack/react-router"
import { Card, CardContent, CardHeader, CardTitle } from "@/design-system/components/ui/card"
import { Badge } from "@/design-system/components/ui/badge"
import { Button } from "@/design-system/components/ui/button"
import { Separator } from "@/design-system/components/ui/separator"
import { Calendar, MapPin, ChevronLeft, Download } from "lucide-react"
import {
    Breadcrumb,
    BreadcrumbItem,
    BreadcrumbLink,
    BreadcrumbList,
    BreadcrumbPage,
    BreadcrumbSeparator,
} from "@/design-system/components/ui/breadcrumb"
import { useSelector } from "react-redux"
import { selectTicketDetailsViewmodel, TicketDetailsViewmodel } from "./ticket-details.viewmodel"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import { FC } from "react"
import { exhaustiveCheck } from "@/lib/exhaustive-check"

export const TicketDetailsPage = () => {
    const { id } = useParams({ from: '/tickets/tickets/$id' })
    const viewmodel = useSelector(selectTicketDetailsViewmodel(id))

    switch (viewmodel.type) {
        case 'loading':
            return <Loading />
        case 'not-found':
            throw (notFound())
        case 'loaded':
            return <Loaded {...viewmodel} />
        default:
            return exhaustiveCheck(viewmodel)
    }
}

const Loaded: FC<Extract<TicketDetailsViewmodel, { type: 'loaded' }>> = (ticket) => {
    return (
        <div className="py-8 space-y-8">
            <Breadcrumb>
                <BreadcrumbList>
                    <BreadcrumbItem>
                        <BreadcrumbLink asChild>
                            <Link to="/tickets">Tickets</Link>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>Booking Details</BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Booking Details</h1>
                <Badge
                    className="text-sm"
                >
                    Confirmed
                </Badge>
            </div>

            <div className="grid gap-6 lg:grid-cols-3">
                <div className="lg:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Event Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="flex gap-4">
                                <img
                                    src={ticket.event.cover}
                                    alt={ticket.event.title}
                                    className="w-32 h-32 object-cover rounded aspect-square"
                                />
                                <div className="flex-1 space-y-4">
                                    <h2 className="text-2xl font-semibold">{ticket.event.title}</h2>
                                    <div className="space-y-2">
                                        <div className="flex items-center gap-2">
                                            <Calendar className="h-4 w-4 text-muted-foreground" />
                                            <span className="text-highlight font-medium">{ticket.event.date}</span>
                                            <span className="text-muted-foreground">•</span>
                                            <span className="text-muted-foreground">{ticket.event.time}</span>
                                        </div>
                                        <div className="flex items-center gap-2 text-muted-foreground">
                                            <MapPin className="h-4 w-4" />
                                            <span>
                                                {ticket.event.venue}, {ticket.event.location}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Ticket Details</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {ticket.tickets.map((t) => (
                                <div key={t.id} className="flex justify-between items-center">
                                    <div className="space-y-1">
                                        <p className="font-medium">{t.type}</p>
                                        <p className="text-sm text-muted-foreground">
                                            Quantity: {t.quantity} × {t.unitPrice}
                                        </p>
                                    </div>
                                    <p className="font-semibold">{t.price}</p>
                                </div>
                            ))}
                            <Separator />
                            <div className="flex justify-between items-center">
                                <span className="text-muted-foreground">Service Fees</span>
                                <span>{ticket.fees}</span>
                            </div>
                            <Separator />
                            <div className="flex justify-between items-center text-lg font-bold">
                                <span>Total</span>
                                <span>{ticket.total}</span>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Actions</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                            <Button className="w-full" variant="default">
                                <Download className="h-4 w-4 mr-2" />
                                Download Tickets
                            </Button>
                            <Button className="w-full" variant="outline" asChild>
                                <Link to="/event/$id" params={{ id: ticket.event.id }}>
                                    View Event Details
                                </Link>
                            </Button>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Purchase Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3 text-sm">
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Booking Date</span>
                                <span className="font-medium">{ticket.booking.bookedAt}</span>
                            </div>
                            <div className="flex justify-between">
                                <span className="text-muted-foreground">Status</span>
                                <span className="font-medium capitalize">Confirmed</span>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>

            <Button variant="outline" asChild>
                <Link to="/tickets">
                    <ChevronLeft className="h-4 w-4 mr-2" />
                    Back to Tickets
                </Link>
            </Button>
        </div>
    )
}

const Loading: FC = () => {
    return (
        <div className="py-8 space-y-8">
            <Breadcrumb>
                <BreadcrumbList>
                    <BreadcrumbItem>
                        <BreadcrumbLink asChild>
                            <Link to="/tickets">Tickets</Link>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>Booking Details</BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div className="flex items-center justify-between">
                <Skeleton className="h-10 w-64" />
                <Skeleton className="h-7 w-24" />
            </div>

            <div className="grid gap-6 lg:grid-cols-3">
                <div className="lg:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Event Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="flex gap-4">
                                <Skeleton className="w-32 h-32 rounded" />
                                <div className="flex-1 space-y-4">
                                    <Skeleton className="h-8 w-3/4" />
                                    <div className="space-y-2">
                                        <Skeleton className="h-5 w-2/3" />
                                        <Skeleton className="h-5 w-full" />
                                    </div>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Ticket Details</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="flex justify-between items-center">
                                <div className="space-y-1">
                                    <Skeleton className="h-5 w-32" />
                                    <Skeleton className="h-4 w-40" />
                                </div>
                                <Skeleton className="h-5 w-16" />
                            </div>
                            <Separator />
                            <div className="flex justify-between items-center">
                                <Skeleton className="h-5 w-24" />
                                <Skeleton className="h-5 w-16" />
                            </div>
                            <Separator />
                            <div className="flex justify-between items-center">
                                <Skeleton className="h-6 w-16" />
                                <Skeleton className="h-6 w-20" />
                            </div>
                        </CardContent>
                    </Card>
                </div>

                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Actions</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                            <Skeleton className="h-10 w-full" />
                            <Skeleton className="h-10 w-full" />
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Purchase Information</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-3">
                            <div className="flex justify-between">
                                <Skeleton className="h-4 w-24" />
                                <Skeleton className="h-4 w-32" />
                            </div>
                            <div className="flex justify-between">
                                <Skeleton className="h-4 w-20" />
                                <Skeleton className="h-4 w-24" />
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>

            <Skeleton className="h-10 w-40" />
        </div>
    )
}

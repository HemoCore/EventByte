import { withLoading } from "@/lib/with-loading"
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/design-system/components/ui/breadcrumb"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import { Link } from "@tanstack/react-router"
import { FC } from "react"

type Props = {
    title: string
}

const EventBreadcrumb: FC<Props> = ({ title }) => {
    return (
        <Breadcrumb className="mb-6">
            <BreadcrumbList>
                <BreadcrumbItem>
                    <BreadcrumbLink asChild>
                        <Link to="/">Home</Link>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbPage>{title}</BreadcrumbPage>
                </BreadcrumbItem>
            </BreadcrumbList>
        </Breadcrumb>
    )
}

const Loading: FC = () => {
    return <Skeleton className="h-6 w-48 mb-6" />
}

export default withLoading(EventBreadcrumb, Loading)

import { withLoading } from "@/lib/with-loading"
import { Skeleton } from "@/design-system/components/ui/skeleton"
import { FC } from "react"

type Props = {
    description: string
}

const EventDescription: FC<Props> = ({ description }) => {
    return (
        <section className="space-y-4">
            <h2 className="text-2xl font-bold">Description</h2>
            <p className="text-lg text-muted-foreground max-w-4xl whitespace-pre-wrap">{description}</p>
        </section>
    )
}

const Loading: FC = () => {
    return (
        <section className="space-y-4">
            <Skeleton className="h-10 w-48" />
            <div className="space-y-2 max-w-4xl">
                <Skeleton className="h-5 w-full" />
                <Skeleton className="h-5 w-full" />
                <Skeleton className="h-5 w-3/4" />
            </div>
        </section>
    )
}

export default withLoading(EventDescription, Loading)

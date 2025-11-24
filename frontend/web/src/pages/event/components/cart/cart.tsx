import { Minus, Plus } from "@/components/icons";
import { Button } from "@/design-system/components/ui/button";
import { Card, CardContent } from "@/design-system/components/ui/card";
import { useAppDispatch } from "@/hooks/store-hooks";
import { FC } from "react";
import { useSelector } from "react-redux";
import { selectCartViewmodel } from "./cart.viewmodel";
import { Tooltip, TooltipContent, TooltipTrigger } from "@/design-system/components/ui/tooltip";
import { Info } from "lucide-react";
import { useNavigate } from "@tanstack/react-router";
import { RequireAuth } from "@/components/require-auth";
import { Separator } from "@/design-system/components/ui/separator";
import {
    Drawer,
    DrawerContent,
} from "@/design-system/components/ui/drawer";
import { useIsMobile } from "@/design-system/hooks/use-is-mobile";

export const Cart: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate()
    const { lines, itemsCount, fees, total, add, remove, book } = useSelector(selectCartViewmodel({
        dispatch,
        onBookSuccess: (id) => {
            navigate({ to: `/booking/$id`, params: { id } })
        }
    }))

    const isMobile = useIsMobile()

    return (
        <>
            <div className="hidden lg:block flex-1">
                <Card>
                    <CardContent className="space-y-6">
                        <div className="space-y-1">
                            <h3 className="text-2xl font-bold">Your Selection</h3>
                            <p className="text-sm text-muted-foreground">
                                {itemsCount} {itemsCount === 1 ? "ticket" : "tickets"}
                            </p>
                        </div>

                        <Separator />

                        <div className="space-y-4">
                            {lines.map((line) => (
                                <div key={line.id} className="space-y-3">
                                    <div className="flex justify-between items-start">
                                        <div className="space-y-1 flex-1">
                                            <p className="font-semibold">{line.name}</p>
                                            <p className="text-sm text-muted-foreground">
                                                {line.quantity} × {line.unitPrice}
                                            </p>
                                        </div>
                                        <p className="font-semibold">{line.total}</p>
                                    </div>
                                    <div className="flex gap-2 items-center">
                                        <Button variant="outline" size='icon-sm' onClick={() => remove(line.id)}>
                                            <Minus />
                                        </Button>
                                        <span className="min-w-8 text-center font-medium">{line.quantity}</span>
                                        <Button variant="outline" size='icon-sm' onClick={() => add(line.id)}>
                                            <Plus />
                                        </Button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <Separator />

                        <div className="space-y-3">
                            <div className="flex justify-between items-center text-sm text-muted-foreground">
                                <span>Service Fees</span>
                                <div className="flex gap-2 items-center">
                                    <Tooltip>
                                        <TooltipTrigger>
                                            <Info className="w-4 h-4" />
                                        </TooltipTrigger>
                                        <TooltipContent className="max-w-60">
                                            Service fees help cover technical costs related to the transaction, ticket issuance, and data security. They are non-refundable. In case of a refund, additional service fees may apply.
                                        </TooltipContent>
                                    </Tooltip>
                                    <span>{fees}</span>
                                </div>
                            </div>
                            <div className="flex justify-between items-center text-lg font-bold">
                                <span>Total</span>
                                <span>{total}</span>
                            </div>
                        </div>

                        <RequireAuth>
                            <Button className="w-full" size="lg" onClick={book}>
                                Book Now
                            </Button>
                        </RequireAuth>
                    </CardContent>
                </Card>
            </div>

            <Drawer open={isMobile} modal={false} dismissible={false}>
                <DrawerContent>
                    <div className="p-4 space-y-4">
                        <div className="space-y-3">
                            <div className="flex justify-between items-center text-sm text-muted-foreground">
                                <span>Service Fees</span>
                                <div className="flex gap-2 items-center">
                                    <Tooltip>
                                        <TooltipTrigger>
                                            <Info className="w-4 h-4" />
                                        </TooltipTrigger>
                                        <TooltipContent className="max-w-60">
                                            Service fees help cover technical costs related to the transaction, ticket issuance, and data security. They are non-refundable. In case of a refund, additional service fees may apply.
                                        </TooltipContent>
                                    </Tooltip>
                                    <span>{fees}</span>
                                </div>
                            </div>
                            <div className="flex justify-between items-center text-lg font-bold">
                                <span>Total</span>
                                <span>{total}</span>
                            </div>
                        </div>

                        <RequireAuth>
                            <Button className="w-full" size="lg" onClick={book}>
                                Book Now
                            </Button>
                        </RequireAuth>
                    </div>
                </DrawerContent>
            </Drawer>
        </>
    )
}

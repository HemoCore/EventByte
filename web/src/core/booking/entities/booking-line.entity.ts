export type BookingLineEntity = {
    id: string
    name: string
    quantity: number
    price: number
}

export const bookingLineEntityBuilder = (entity: BookingLineEntity = {
    id: 'line-1',
    name: 'Early Bird',
    quantity: 1,
    price: 0
}) => ({
    withId: (id: string) => bookingLineEntityBuilder({ ...entity, id }),
    withQuantity: (quantity: number) => bookingLineEntityBuilder({ ...entity, quantity }),
    withName: (name: string) => bookingLineEntityBuilder({ ...entity, name }),
    withPrice: (price: number) => bookingLineEntityBuilder({ ...entity, price }),
    build: () => entity
})

import amqp, { Channel, ChannelModel } from 'amqplib';

const CHANNEL_NAME = "events"
const RECONNECT_DELAY = 10000

type RabbitMQState = {
    send(type: string, data: Record<string, unknown>): boolean
}

class DisconnectedState implements RabbitMQState {
    send(): boolean {
        return false
    }
}

class ConnectedState implements RabbitMQState {
    constructor(
        private readonly channel: Channel,
    ) { }

    send(type: string, data: Record<string, unknown>): boolean {
        return this.channel.publish(
            CHANNEL_NAME,
            type,
            Buffer.from(JSON.stringify(data)),
            { persistent: true }
        )
    }
}

export class RabbitMQ {
    private state: RabbitMQState = new DisconnectedState()

    private constructor(private readonly url: string) { }

    static async connect(url: string): Promise<RabbitMQ> {
        const instance = new RabbitMQ(url)
        await instance.initConnection()
        return instance
    }

    private async initConnection(): Promise<void> {
        try {
            const connection = await this.createConnection()
            const channel = await this.createChannel(connection)

            this.state = new ConnectedState(channel)
            strapi.log.info("✅ Connected to RabbitMQ")
        } catch (error) {
            const e = error as Error
            strapi.log.error(`RabbitMQ ❌ Connection failed: ${e.stack}`,)
            this.scheduleReconnect()
        }
    }

    private async createConnection(): Promise<ChannelModel> {
        const connection = await amqp.connect(this.url)

        connection.on("close", () => {
            this.state = new DisconnectedState()
            this.scheduleReconnect()
        })

        connection.on("error", (err) => {
            strapi.log.error("RabbitMQ ⚠️ Connection error:", err.message)
        })

        return connection
    }

    private async createChannel(connection: ChannelModel): Promise<Channel> {
        const channel = await connection.createChannel()
        await channel.assertExchange(CHANNEL_NAME, "topic", { durable: true })
        return channel
    }

    private scheduleReconnect(): void {
        setTimeout(() => this.initConnection(), RECONNECT_DELAY)
    }

    send(type: string, data: Record<string, unknown>): boolean {
        return this.state.send(type, data)
    }
}

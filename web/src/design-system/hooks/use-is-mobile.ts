import { useEffect, useState } from "react"

export function useIsMobile(): boolean {
	const [isMobile, setIsMobile] = useState(false)

	useEffect(() => {
		const mediaQuery = window.matchMedia("(max-width: 1023px)")

		const handleChange = () => {
			setIsMobile(mediaQuery.matches)
		}

		// Set initial value
		handleChange()

		// Listen for changes
		mediaQuery.addEventListener("change", handleChange)

		return () => mediaQuery.removeEventListener("change", handleChange)
	}, [])

	return isMobile
}

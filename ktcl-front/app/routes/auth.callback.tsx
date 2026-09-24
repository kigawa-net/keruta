import {useEffect} from "react"
import {useSearchParams} from "react-router"

export default function AuthCallback() {
    const [searchParams] = useSearchParams()

    useEffect(() => {
        const token = searchParams.get("token")
        if (token) {
            sessionStorage.setItem("kise_token", token)
        }
        window.location.replace("/")
    }, [])

    return <div>ログイン処理中...</div>
}

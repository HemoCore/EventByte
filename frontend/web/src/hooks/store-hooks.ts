import { AppDispatch } from "@/core/create-store";
import { useDispatch } from "react-redux";

export const useAppDispatch = useDispatch<AppDispatch>;

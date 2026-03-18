import OrderPageClient from "./OrderPageClient";

export function generateStaticParams() {
  return [
    { occasion: "birthday" },
    { occasion: "wedding" },
    { occasion: "anniversary" },
    { occasion: "baby-shower" },
    { occasion: "festival" },
    { occasion: "corporate" },
    { occasion: "custom" },
  ];
}

export default function OrderPage() {
  return <OrderPageClient />;
}

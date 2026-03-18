import StatusPageClient from "./StatusPageClient";

export function generateStaticParams() {
  return [{ orderId: "demo-order-123" }];
}

export default function StatusPage() {
  return <StatusPageClient />;
}

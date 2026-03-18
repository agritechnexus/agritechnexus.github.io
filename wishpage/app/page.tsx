import Hero from "@/components/landing/Hero";
import HowItWorks from "@/components/landing/HowItWorks";
import OccasionGrid from "@/components/landing/OccasionGrid";
import PricingCards from "@/components/landing/PricingCards";
import SocialProof from "@/components/landing/SocialProof";
import BottomCTA from "@/components/landing/BottomCTA";

export default function Home() {
  return (
    <>
      <Hero />
      <HowItWorks />
      <OccasionGrid />
      <PricingCards />
      <SocialProof />
      <BottomCTA />
    </>
  );
}

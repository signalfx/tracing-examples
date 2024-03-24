<?php

namespace App\Controller;

use OpenTelemetry\API\Globals;
use OpenTelemetry\API\Metrics\CounterInterface;
use OpenTelemetry\API\Metrics\MeterInterface;
use OpenTelemetry\API\Metrics\ObservableGaugeInterface;
use OpenTelemetry\API\Metrics\ObserverInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/demo')]
final class DemoController extends AbstractController
{
    private CounterInterface $demoCounter;
    private ObservableGaugeInterface $demoGauge;
    private int $gaugeValue = 10;

    public function __construct(
        private LoggerInterface $logger,
    ) {
        $meter = Globals::meterProvider()->getMeter('demo-meter');
        $this->demoCounter = $meter->createCounter('demo-counter');
        $this->demoGauge = $meter->createObservableGauge('demo-gauge');
        $this->demoGauge->observe(function (ObserverInterface $observer): void {
            $observer->observe($this->gaugeValue);
        });
    }

    #[Route('/', name: 'demo_index', methods: ['GET'])]
    public function index(): Response
    {
        $this->logger->info('This is a sample log message.');

        return new Response(
            "Index"
        );
    }

    #[Route('/ready', name: 'demo_ready', methods: ['GET'])]
    public function ready(): Response
    {
        return new Response(
            "Ready"
        );
    }

    #[Route('/metric-count', name: 'demo_metric_count', methods: ['GET'])]
    public function metricCount(): Response
    {
        $this->demoCounter->add(1, ['demo-attribute' => 'demo-value']);

        return new Response(
            "Incremented: demo-counter"
        );
    }

    #[Route('/metric-gauge', name: 'demo_metric_gauge', methods: ['GET'])]
    public function metricGauge(Request $request): Response
    {
        $value = $message = $request->query->has('value') ? intval($request->query->get('value')) : '50';
        $this->gaugeValue = $value;

        return new Response(
            "Set: demo-gauge to $value"
        );
    }
}

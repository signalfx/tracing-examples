FROM golang:1.22 AS builder
RUN addgroup gouser && \
     adduser --ingroup gouser --uid 10001 --shell /bin/false gouser && \
     grep gouser /etc/passwd > /etc/passwd_gouser
WORKDIR /app
COPY go.* ./
RUN go mod download
COPY *.go .
RUN CGO_ENABLED=0 go build -o app

FROM scratch
USER gouser
COPY --from=builder /etc/passwd_gouser /etc/passwd
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/app /app
ENTRYPOINT ["/app"]

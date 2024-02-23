FROM golang:1.22 AS builder
RUN useradd -u 10001 gouser
WORKDIR /app
COPY go.* ./
RUN go mod download
COPY *.go .
RUN CGO_ENABLED=0 go build -o app

FROM scratch
USER gouser
COPY --from=builder /etc/passwd /etc/passwd
COPY --from=builder /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=builder /app/app /app
ENTRYPOINT ["/app"]

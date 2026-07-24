"""
Generates a full, internally-consistent dataset for every entity as CSV files
under data/. Entities are built in dependency order so downstream entities can
reference the fields of the ones built before them:

    theaters, users, movies  ->  showtimes (needs movies + theaters)
                              ->  bookings   (needs users + showtimes)

Run with: python generate_data.py
"""

import csv
import random
from datetime import date, datetime, timedelta

from faker import Faker

fake = Faker()
Faker.seed(42)
random.seed(42)

NUM_MOVIES = 50_000
NUM_USERS = 5_000
NUM_THEATERS = 1_000
NUM_SHOWTIMES = 2_000
NUM_BOOKINGS = 7_500

GENRES = ["Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance", "Thriller", "Adventure", "Animation", "Documentary"]
LANGUAGES = ["English", "French", "Spanish", "Hindi", "Korean", "Japanese", "German", "Mandarin"]
DIRECTORS = [
    "Christopher Nolan", "Steven Spielberg", "Bong Joon-ho", "Martin Scorsese", "Greta Gerwig",
    "Denis Villeneuve", "Quentin Tarantino", "Ava DuVernay", "Wes Anderson", "Jordan Peele",
    "Taika Waititi", "Chloe Zhao", "Ridley Scott", "James Cameron", "Sofia Coppola",
]
TAGS = ["dreams", "heist", "space", "time travel", "family", "crime", "love", "war", "future",
        "revenge", "friendship", "survival", "mystery", "coming-of-age", "underdog"]
SHOWTIME_SLOTS = ["10:00:00", "13:00:00", "16:00:00", "19:00:00", "21:30:00"]

MOVIE_START_DATE = date(1980, 1, 1)
MOVIE_DATE_RANGE_DAYS = (date.today() - MOVIE_START_DATE).days

SHOWTIME_START_DATE = date.today() - timedelta(days=30)
SHOWTIME_END_DATE = date.today() + timedelta(days=45)
SHOWTIME_DATE_RANGE_DAYS = (SHOWTIME_END_DATE - SHOWTIME_START_DATE).days


def generate_movies():
    movies = []
    for i in range(1, NUM_MOVIES + 1):
        movies.append({
            "id": i,
            "title": fake.sentence(nb_words=3).rstrip("."),
            "genres": random.sample(GENRES, random.randint(1, 3)),
            "language": random.sample(LANGUAGES, random.randint(1, 2)),
            "duration": random.randint(80, 180),
            "description": fake.paragraph(nb_sentences=3),
            "director": random.choice(DIRECTORS),
            "rating": round(random.uniform(1.0, 10.0), 1),
            "releaseDate": MOVIE_START_DATE + timedelta(days=random.randint(0, MOVIE_DATE_RANGE_DAYS)),
            "isActive": random.random() < 0.85,
            "tags": random.sample(TAGS, random.randint(1, 3)),
        })
    return movies


def generate_theaters():
    theaters = []
    for i in range(1, NUM_THEATERS + 1):
        theaters.append({
            "theaterId": i,
            "theaterName": f"{fake.company()} Cinemas",
            "theaterAddress": fake.street_address(),
            "theaterCity": fake.city(),
            "theaterState": fake.state_abbr(),
            "screensTotal": random.randint(4, 20),
        })
    return theaters


def generate_users():
    users = []
    for i in range(1, NUM_USERS + 1):
        role = "ADMIN" if random.random() < 0.02 else "USER"
        users.append({
            "userId": i,
            "userFirstName": fake.first_name(),
            "userLastName": fake.last_name(),
            "userEmail": fake.unique.email(),
            "role": role,
        })
    return users


def generate_showtimes(movies, theaters):
    active_movies = [m for m in movies if m["isActive"]]
    showtimes = []
    for i in range(1, NUM_SHOWTIMES + 1):
        movie = random.choice(active_movies)
        theater = random.choice(theaters)
        total_seats = random.choice([80, 100, 120, 150, 180, 200, 250, 300])
        showtimes.append({
            "showtimeId": i,
            "movieTitle": movie["title"],
            "theaterName": theater["theaterName"],
            "localDate": SHOWTIME_START_DATE + timedelta(days=random.randint(0, SHOWTIME_DATE_RANGE_DAYS)),
            "localTime": random.choice(SHOWTIME_SLOTS),
            "totalSeats": total_seats,
            "availableSeats": total_seats,  # decremented as bookings are generated
            "ticketPrice": round(random.uniform(8.0, 25.0), 2),
        })
    return showtimes


def generate_bookings(users, showtimes):
    bookings = []
    today = date.today()
    for i in range(1, NUM_BOOKINGS + 1):
        user = random.choice(users)
        # A handful of retries in case the first showtime picked has no room left.
        for _ in range(5):
            showtime = random.choice(showtimes)
            seat_count = random.randint(1, 6)
            status = "CONFIRMED" if random.random() < 0.85 else "CANCELLED"
            if status == "CANCELLED" or showtime["availableSeats"] >= seat_count:
                break
        else:
            continue  # couldn't find room after retries, skip this booking

        if status == "CONFIRMED":
            showtime["availableSeats"] -= seat_count

        show_date = showtime["localDate"]
        booking_date = show_date - timedelta(days=random.randint(0, 14))
        if booking_date > today:
            booking_date = today

        bookings.append({
            "bookingId": i,
            "userId": user["userId"],
            "userEmail": user["userEmail"],
            "showtimeId": showtime["showtimeId"],
            "movieTitle": showtime["movieTitle"],
            "theaterName": showtime["theaterName"],
            "seatCount": seat_count,
            "totalPrice": round(seat_count * showtime["ticketPrice"], 2),
            "bookingTime": booking_date,
            "status": status,
        })
    return bookings


def write_csv(path, rows, fieldnames, list_fields=()):
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(fieldnames)
        for row in rows:
            values = []
            for field in fieldnames:
                value = row[field]
                if field in list_fields:
                    value = ", ".join(value)
                elif isinstance(value, bool):
                    value = "true" if value else "false"
                elif isinstance(value, (date, datetime)):
                    value = value.strftime("%Y-%m-%d")
                values.append(value)
            writer.writerow(values)


def main():
    print(f"Generating {NUM_THEATERS} theaters...")
    theaters = generate_theaters()
    write_csv("data/theaters.csv", theaters,
              ["theaterId", "theaterName", "theaterAddress", "theaterCity", "theaterState", "screensTotal"])

    print(f"Generating {NUM_USERS} users...")
    users = generate_users()
    write_csv("data/users.csv", users,
              ["userId", "userFirstName", "userLastName", "userEmail", "role"])

    print(f"Generating {NUM_MOVIES} movies...")
    movies = generate_movies()
    write_csv("data/movies.csv", movies,
              ["id", "title", "genres", "language", "duration", "description", "director",
               "rating", "releaseDate", "isActive", "tags"],
              list_fields=("genres", "language", "tags"))

    print(f"Generating {NUM_SHOWTIMES} showtimes...")
    showtimes = generate_showtimes(movies, theaters)

    print(f"Generating {NUM_BOOKINGS} bookings...")
    bookings = generate_bookings(users, showtimes)

    # availableSeats on showtimes was mutated while generating bookings, so
    # showtimes.csv is written last with the final, post-booking counts.
    write_csv("data/showtimes.csv", showtimes,
              ["showtimeId", "movieTitle", "theaterName", "localDate", "localTime",
               "totalSeats", "availableSeats", "ticketPrice"])
    write_csv("data/bookings.csv", bookings,
              ["bookingId", "userId", "userEmail", "showtimeId", "movieTitle", "theaterName",
               "seatCount", "totalPrice", "bookingTime", "status"])

    print("Done. Wrote theaters.csv, users.csv, movies.csv, showtimes.csv, bookings.csv to data/")


if __name__ == "__main__":
    main()
